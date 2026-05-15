-- Phase 2.5: Stock adjustment audit log.
-- One row per intentional change to stock outside the regular receive/pick flow:
--   CYCLE_COUNT      — physical count overrides the system qty at (item, section)
--   DAMAGE           — qty moves from a normal section to the damages section
--   WRITE_OFF        — qty disappears (lost, expired, scrapped)
--   TRANSFER         — qty moves between two warehouse sections

create sequence stock_adjustment_SEQ start with 1 increment by 50;

create table stock_adjustment (
    id bigint not null,
    type varchar(16) not null check (type in ('CYCLE_COUNT','DAMAGE','WRITE_OFF','TRANSFER')),
    item_id bigint not null,
    source_warehouse_section_id bigint not null,
    destination_warehouse_section_id bigint,
    qty integer not null,
    adjustment_date date not null,
    notes varchar(255),
    primary key (id)
);

alter table if exists stock_adjustment
    add constraint FKstockadj_item
    foreign key (item_id)
    references item;

alter table if exists stock_adjustment
    add constraint FKstockadj_source
    foreign key (source_warehouse_section_id)
    references warehouse_section;

alter table if exists stock_adjustment
    add constraint FKstockadj_destination
    foreign key (destination_warehouse_section_id)
    references warehouse_section;
