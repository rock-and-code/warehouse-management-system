# Warehouse Management System

A self-contained Spring Boot web app for running a small-business warehouse: customers, vendors, items, sales orders, purchase orders, goods receipt notes, put-aways, picking jobs, invoices, and backorders — all gated by a real authentication layer with optional email-based two-factor auth.

![Dashboard](docs/screenshots/03-dashboard-hero.png)

---

## Highlights

- **Operational dashboard** — KPI cards (open SOs, open POs, pending GRNs, backorders) + inventory snapshot tiles (value, units, OOS, low-stock) + Chart.js daily-sales line chart, top-vendors-by-YTD-spend bar chart, and a PO-status doughnut. Two action tables surface items that need reordering and POs aging past 30 days. Top 5 movers stays.
- **Authentication built in** — Spring Security 6 form login, BCrypt password hashing, optional **email 2FA** (6-digit code per sign-in), password reset via email link, and an admin-only `/admin/users` area for user CRUD.
- **Left-sidebar UI** — dark fixed-rail navigation with Bootstrap Icons, collapsible groups for each domain area, and a Bootstrap offcanvas drawer on mobile. The topbar shows the current page title, signed-in username, and sign-out.
- **Realistic seeded data** — on first boot the app generates ~200 sales orders, ~150 purchase orders, ~1100 warehouse sections, ~400 stock rows, invoices, and backorders, all dated within the current year so dashboard charts populate immediately.
- **Tech stack** — Java 17 · Spring Boot 3.2 · Spring Security 6 · Spring Data JPA · Thymeleaf · Bootstrap 5 · Chart.js 4 · H2 (in-memory) · Lombok · Maven.

---

## Screenshots

### Landing page (dashboard)
The login lands here. KPI strip across the top, inventory snapshot, 30-day daily-sales chart, vendor spend, PO status doughnut, reorder list, top movers, and PO aging.

![Dashboard, full page](docs/screenshots/02-dashboard.png)

### Sidebar navigation
Each section expands inline. Page title and signed-in user appear in the topbar.

![Sidebar expanded](docs/screenshots/04-sidebar-expanded.png)

### Sign in
Form login at `/login`. Forgot-password link sends a reset email.

![Sign in](docs/screenshots/01-login.png)

### Two-factor verification
Users with 2FA enabled are diverted to `/verify-2fa` after a correct password. A 6-digit code is generated, BCrypt-hashed, and emailed (logged to the console as a dev fallback when SMTP isn't configured).

![Two-factor verification](docs/screenshots/06-verify-2fa.png)

### Forgot password
Always shows the same banner, regardless of whether the email matches an account — no enumeration.

![Forgot password](docs/screenshots/07-forgot-password.png)

### Admin user management
`/admin/users` — list, create, edit, enable/disable, toggle 2FA, delete. ROLE_ADMIN only.

![Admin users](docs/screenshots/05-admin-users.png)

---

## Run it

Java 17 and the bundled Maven wrapper are all you need.

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8080> and sign in with one of the seeded accounts:

| Username  | Password        | Role   | 2FA |
|-----------|-----------------|--------|-----|
| `admin`   | `Password123!`  | ADMIN  | off |
| `manager` | `Password123!`  | USER   | on  |

Signing in as `manager` triggers the 2FA flow — the 6-digit code is printed to the app log (search for `Your verification code is:`) since SMTP is not configured by default. To send real emails, set `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, and `MAIL_FROM` environment variables before launch.

### Useful commands

```bash
./mvnw spring-boot:run                                # run on :8080
./mvnw clean package                                  # build the jar
./mvnw test                                           # run tests
./mvnw -Dtest=TrieTest test                           # run one test class
./mvnw -Dtest=TrieTest#getWordList test               # run one test method
```

The H2 console is exposed at <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:testdb`, user `SA`, no password) for ad-hoc SQL against the running database.

---

## Core domain workflow

Inbound and outbound flows share a small entity vocabulary:

1. **Purchase order** → on receipt, a **goods receipt note (GRN)** is opened against it.
2. Fulfilling the GRN creates **stock** rows on a floor section (`00-00-0-0`).
3. **Put-away** moves stock from the floor to a real warehouse section.
4. Saving a **sales order** also creates a **picking job** that mirrors each order line.
5. Fulfilling the picking job decrements stock, writes an **invoice**, and creates **backorders** for unmet quantities.

The dashboard queries surface every link in that chain.

---

## Project layout

```
src/main/java/com/example/warehouseManagement/
├── Bootstrap/           # CommandLineRunner that seeds the in-memory DB
├── Controllers/         # Spring MVC controllers (Thymeleaf views)
├── Domains/             # JPA entities + nested status enums
│   ├── DTOs/            # Projection DTOs returned by native queries
│   └── Exceptions/      # Domain exceptions caught by controllers
├── Repositories/        # Spring Data CrudRepository interfaces (many native @Query)
├── Security/            # SecurityConfig + 2FA AuthenticationSuccessHandler
├── Services/            # Interface + Impl per capability
├── Util/                # Small standalone helpers
└── DSA/                 # Trie + BinarySearchTree (used by tests)

src/main/resources/
├── application.properties
└── templates/
    ├── admin/           # User CRUD pages (ROLE_ADMIN)
    ├── auth/            # Login, verify-2fa, forgot/reset password
    ├── customers/       # Customer CRUD
    ├── dashboard/       # Operational dashboard
    ├── fragments/       # Shared header/navbar/footer fragments
    ├── goodsReceiptNotes/ items/ itemPrices/
    ├── pickingJobs/ purchaseOrders/ putAwayTasks/
    ├── reports/ salesOrders/ vendors/
```

For a more detailed map (commands, fragment behavior, H2-specific SQL idioms, gotchas), see [`CLAUDE.md`](CLAUDE.md) at the repo root.

---

## Gotchas

- **In-memory database.** Every restart wipes the DB and `Bootstrap` re-seeds. Persistent storage is on the roadmap (file-based H2 + Flyway migrations).
- **H2-specific SQL.** Native queries use H2 dialect (`TO_CHAR`, `DATEDIFF('DAY', …)`, `EXTRACT(WEEK FROM CURRENT_DATE())`, `NVL`). Swapping databases will require rewriting the report and dashboard queries.
- **Default seed credentials are dev-only.** Change them or wipe the seed before exposing the app outside localhost.

---

## License

MIT.
