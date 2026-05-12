# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 3.2.1 on Java 17, server-side rendered with Thymeleaf, persisted to an in-memory H2 database. Maven build via the bundled wrapper. Lombok generates entity boilerplate (`@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`). Spring Security with form login + email-based 2FA + password reset; thymeleaf-extras-springsecurity6 is on the classpath for `sec:authorize`.

## Commands

```bash
./mvnw spring-boot:run                                    # run app on :8080
./mvnw test                                               # run all tests
./mvnw -Dtest=TrieTest test                               # run one test class
./mvnw -Dtest=TrieTest#getWordList test                   # run one test method
./mvnw clean package                                      # build jar to target/
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, no auth needed — `application.properties` enables the console and uses `ddl-auto=update`).

## Architecture

Classic Spring MVC layering, packaged under `com.example.warehouseManagement`:

- `Domains/` — JPA entities (`@Entity` + Lombok). Status enums live as nested types on their owning entity (`SalesOrder.SoStatus`, `PurchaseOrder.PoStatus`, `GoodsReceiptNote.GrnStatus`, `PickingJob.PjStatus`).
- `Domains/DTOs/` — Projection DTOs returned by native queries, plus form-binding DTOs.
- `Domains/Exceptions/` — Domain exceptions thrown from services and caught in controllers to drive redirects (e.g. `ShippedOrderModificationException`).
- `Repositories/` — `CrudRepository` interfaces. Many methods are `@Query(nativeQuery = true)` returning DTO projections. Queries use **H2-specific SQL** (`TO_CHAR`, `DATEDIFF`, `FORMATDATETIME`, `DATEADD`, `EXTRACT`); changing databases will require rewriting them.
- `Services/` — Each capability is split into a `FooService` interface and `FooServiceImpl`. Wire services together (not repositories) when crossing capabilities.
- `Controllers/` — Thymeleaf-returning controllers (return view names, not `@RestController`). All controllers `@RequestMapping(value = "/")` except `ReportController` (`/reports`). Each controller defines path constants at the top.
- `Bootstrap/Bootstrap.java` — `CommandLineRunner` that seeds customers, vendors, items, prices, costs, warehouse sections, and sample sales/purchase orders on every startup (because the DB is in-memory). Touch this when changing entity shape or when reproducing data-dependent bugs.
- `DSA/` (`Trie`, `BinarySearchTree`) and `Util/Counter` — standalone utilities. As of now, `Trie` and `BinarySearchTree` are only referenced by tests, not by main code.
- `Security/` — `SecurityConfig` (filter chain, BCrypt) and `TwoFactorAuthenticationSuccessHandler` (post-password-auth hook).

### Auth flow

- `UserService` doubles as `UserDetailsService`. The `User` entity stores the 2FA code (BCrypt-hashed) and password-reset token, each with an expiry timestamp — there is no separate token table.
- Form login posts to `/login`. After Spring Security validates credentials, `TwoFactorAuthenticationSuccessHandler` checks `user.twoFactorEnabled`:
  - If on: generates a 6-digit code, emails it, **overwrites the session's saved `SecurityContext` with an empty one** via `SecurityContextRepository.saveContext(...)`, sets `PENDING_2FA_USERNAME` on the session, and redirects to `/verify-2fa`. The overwrite is required — clearing only `SecurityContextHolder` is not enough because `UsernamePasswordAuthenticationFilter` already persisted the authenticated context to the session before the success handler runs. Removing it would re-open a 2FA bypass.
  - If off: redirects to `/`.
- `/verify-2fa` POST validates the code, builds a fresh `UsernamePasswordAuthenticationToken`, and writes it back via `SecurityContextRepository` so subsequent requests are fully authenticated.
- `EmailServiceImpl` always tries `JavaMailSender.send(...)`; if SMTP isn't configured (default in dev) it catches `MailException` and **logs the full email body** so codes and reset links can be copied from the console. Don't remove this fallback without providing an alternative dev path.
- Forgot-password always returns the same banner regardless of whether the email matched a user (no enumeration).
- `/admin/**` requires `ROLE_ADMIN`. The H2 console under `/h2-console/**` is permit-all with `frameOptions=sameOrigin` and CSRF disabled for that path only.
- Seed users (created on each in-memory startup by `Bootstrap.seedUsers`): `admin` / `Password123!` (ADMIN, no 2FA) and `manager` / `Password123!` (USER, 2FA on). Change these before any non-dev deployment.

### Controller form conventions

Forms that build up a list of lines (sales orders, purchase orders, GRNs) all use the same pattern: a single endpoint differentiated by a request parameter, returning the same template each time except for save.

```java
@PostMapping(value = NEW_SALES_ORDER_PATH, params = "addRow")     // append a blank line
@PostMapping(value = NEW_SALES_ORDER_PATH, params = "removeRow")  // remove line at index
@PostMapping(value = NEW_SALES_ORDER_PATH, params = "save")       // persist and redirect
```

After mutations, controllers redirect to a list URL with a result flag in the query string (`?added`, `?updated`, `?notFound`, `?salesOrderDeleted`, `?cannotBeUpdated`, `?failedToDelete`). Templates read these flags to render banners. Preserve this pattern when adding new flows — don't introduce flash attributes or JSON responses without a reason.

### Core domain workflow

The entities cooperate as a small WMS pipeline; understanding this saves cross-file reading:

1. **Inbound**: `PurchaseOrder` → on receipt, a `GoodsReceiptNote` is opened against it → fulfilling the GRN creates `Stock` rows on a floor `WarehouseSection` (constant `PutAwayController.FLOOR = "00-00-0-0"`).
2. **Put-away**: `PutAwayController` moves `Stock` from the floor section to a real `WarehouseSection`.
3. **Outbound**: Saving a `SalesOrder` (`SalesOrderServiceImpl.save`) **also** creates a `PickingJob` with `PickingJobLine`s mirroring each `SalesOrderLine`. Fulfilling the picking job decrements `Stock`, writes an `Invoice`, and creates `Backorder`s for unmet quantities. Status transitions: `PENDING` → `PARTIALLY_SHIPPED` → `SHIPPED`.
4. **Pricing**: `ItemPrice` and `ItemCost` are time-versioned per item — order lines snapshot the *current* price/cost via `findCurrentItemPriceByItemId` at save time so historical totals stay correct.

A shipped sales order is immutable: `SalesOrderServiceImpl.updateById` throws `ShippedOrderModificationException`. Honor this invariant — don't add bypasses.

## Gotchas

- `ddl-auto=update` plus the always-on `Bootstrap` runner means schema changes are picked up on restart but old seed data persists only for the lifetime of the JVM. Restart the app to reset state.
- When adding an entity field, check `Bootstrap.java` — it builds entities by hand and will fail to compile if required fields are added without defaults.
- Native queries return DTO interfaces (projections). Don't replace them with entity returns without updating the SQL `AS` aliases to match getters.
- `pom.xml` excludes Lombok from the Spring Boot fat jar build — this is intentional; don't "fix" it.
