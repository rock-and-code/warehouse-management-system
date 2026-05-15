-- Baseline schema generated from JPA entities (Hibernate 6.4 DDL output) on 2026-05-14.
-- Captures the schema that was previously created at runtime by spring.jpa.hibernate.ddl-auto=update.
-- From here forward, all schema changes must ship as a new V{n}__*.sql migration.

create sequence app_user_SEQ start with 1 increment by 50;
create sequence backorder_SEQ start with 1 increment by 50;
create sequence customer_SEQ start with 1 increment by 50;
create sequence goods_receipt_note_line_SEQ start with 1 increment by 50;
create sequence goods_receipt_note_SEQ start with 1 increment by 50;
create sequence invoice_line_SEQ start with 1 increment by 50;
create sequence invoice_SEQ start with 1 increment by 50;
create sequence item_cost_SEQ start with 1 increment by 50;
create sequence item_price_SEQ start with 1 increment by 50;
create sequence item_SEQ start with 1 increment by 50;
create sequence picking_job_line_SEQ start with 1 increment by 50;
create sequence picking_job_SEQ start with 1 increment by 50;
create sequence purchase_order_line_SEQ start with 1 increment by 50;
create sequence purchase_order_SEQ start with 1 increment by 50;
create sequence sales_order_line_SEQ start with 1 increment by 50;
create sequence sales_order_SEQ start with 1 increment by 50;
create sequence stock_SEQ start with 1 increment by 50;
create sequence vendor_SEQ start with 1 increment by 50;
create sequence warehouse_section_SEQ start with 1 increment by 50;
create sequence warehouse_SEQ start with 1 increment by 50;

create table app_user (
    enabled boolean not null,
    two_factor_enabled boolean not null,
    created_at timestamp(6) with time zone not null,
    id bigint not null,
    last_login_at timestamp(6) with time zone,
    password_reset_token_expires_at timestamp(6) with time zone,
    two_factor_code_expires_at timestamp(6) with time zone,
    role varchar(16) not null check (role in ('USER','ADMIN')),
    username varchar(64) not null unique,
    email varchar(254) not null unique,
    password varchar(255) not null,
    password_reset_token varchar(255),
    two_factor_code varchar(255),
    primary key (id)
);

create table backorder (
    qty integer,
    id bigint not null,
    item_id bigint,
    sales_order_id bigint,
    primary key (id)
);

create table customer (
    id bigint not null,
    city varchar(255),
    contact_info varchar(255),
    name varchar(255),
    state varchar(255),
    street varchar(255),
    zipcode varchar(255),
    primary key (id)
);

create table goods_receipt_note (
    date date,
    status tinyint check (status between 0 and 1),
    id bigint not null,
    purchase_order_id bigint,
    primary key (id)
);

create table goods_receipt_note_line (
    qty integer,
    goods_receipt_note_id bigint,
    id bigint not null,
    item_id bigint,
    notes varchar(255),
    primary key (id)
);

create table invoice (
    date date,
    customer_id bigint,
    id bigint not null,
    sales_order_id bigint,
    primary key (id)
);

create table invoice_line (
    qty integer,
    id bigint not null,
    invoice_id bigint,
    item_id bigint,
    primary key (id)
);

create table item (
    sku integer,
    id bigint not null,
    vendor_id bigint,
    description varchar(255),
    primary key (id)
);

create table item_cost (
    cost float(53),
    end_date date,
    start_date date,
    id bigint not null,
    item_id bigint,
    primary key (id)
);

create table item_price (
    end_date date,
    price float(53),
    start_date date,
    id bigint not null,
    item_id bigint,
    primary key (id)
);

create table picking_job (
    date date,
    status tinyint check (status between 0 and 1),
    id bigint not null,
    sales_order_id bigint,
    primary key (id)
);

create table picking_job_line (
    qty_picked integer,
    qty_to_pick integer,
    id bigint not null,
    item_id bigint,
    picking_job_id bigint,
    warehouse_section_id bigint,
    primary key (id)
);

create table purchase_order (
    date date,
    status tinyint check (status between 0 and 2),
    id bigint not null,
    vendor_id bigint,
    primary key (id)
);

create table purchase_order_line (
    qty integer,
    id bigint not null,
    item_cost_id bigint,
    item_id bigint,
    purchase_order_id bigint,
    primary key (id)
);

create table sales_order (
    date date,
    status tinyint check (status between 0 and 2),
    customer_id bigint,
    id bigint not null,
    primary key (id)
);

create table sales_order_line (
    qty integer,
    id bigint not null,
    item_id bigint,
    item_price_id bigint,
    sales_order_id bigint,
    primary key (id)
);

create table stock (
    qty_on_hand integer,
    id bigint not null,
    item_id bigint,
    warehouse_section_id bigint,
    primary key (id)
);

create table vendor (
    id bigint not null,
    city varchar(255),
    contact_info varchar(255),
    name varchar(255),
    state varchar(255),
    street varchar(255),
    zipcode varchar(255),
    primary key (id)
);

create table warehouse (
    id bigint not null,
    address varchar(255),
    primary key (id)
);

create table warehouse_section (
    id bigint not null,
    warehouse_id bigint,
    section_number varchar(255),
    primary key (id)
);

alter table if exists backorder
   add constraint FK2lidbe795js0waw9y7qgcbyof
   foreign key (item_id)
   references item;

alter table if exists backorder
   add constraint FK3nf25tn038uqnnnga7jr5kawt
   foreign key (sales_order_id)
   references sales_order;

alter table if exists goods_receipt_note
   add constraint FKk9poy9h98uh2yrjj8tg394svc
   foreign key (purchase_order_id)
   references purchase_order;

alter table if exists goods_receipt_note_line
   add constraint FK2q4u6nejdxme2sh9am5ko8d23
   foreign key (goods_receipt_note_id)
   references goods_receipt_note;

alter table if exists goods_receipt_note_line
   add constraint FKeuug3twfd4a11k79dwngqiuqb
   foreign key (item_id)
   references item;

alter table if exists invoice
   add constraint FK5e32ukwo9uknwhylogvta4po6
   foreign key (customer_id)
   references customer;

alter table if exists invoice
   add constraint FKr5gx0k42l9gs6blo3f6witivc
   foreign key (sales_order_id)
   references sales_order;

alter table if exists invoice_line
   add constraint FKfnwks1ouvwbttl0fklxsem7ik
   foreign key (invoice_id)
   references invoice;

alter table if exists invoice_line
   add constraint FKfns13dxn3xubvu48h2nlv2k4b
   foreign key (item_id)
   references item;

alter table if exists item
   add constraint FKsx3e4orr16c98r26obqorcny3
   foreign key (vendor_id)
   references vendor;

alter table if exists item_cost
   add constraint FKawq1mt4ays9j5gt12fpynrus2
   foreign key (item_id)
   references item;

alter table if exists item_price
   add constraint FKe6ivp7ms01x65oq2amsv5hrl0
   foreign key (item_id)
   references item;

alter table if exists picking_job
   add constraint FKd5fbqe1lr723lj0gaa2e3ut02
   foreign key (sales_order_id)
   references sales_order;

alter table if exists picking_job_line
   add constraint FKle1ko1s39b2pujh5l73srq8u0
   foreign key (item_id)
   references item;

alter table if exists picking_job_line
   add constraint FKf1wnm3he8yrxt7g0ymgght1kx
   foreign key (picking_job_id)
   references picking_job;

alter table if exists picking_job_line
   add constraint FKr38swjbqvga2765bog9ukc7dm
   foreign key (warehouse_section_id)
   references warehouse_section;

alter table if exists purchase_order
   add constraint FK20jcn7pw6hvx0uo0sh4y1d9xv
   foreign key (vendor_id)
   references vendor;

alter table if exists purchase_order_line
   add constraint FK94ont48m2ohe7rq9lf2tdd5f1
   foreign key (item_id)
   references item;

alter table if exists purchase_order_line
   add constraint FKg51nphncbqcm8ypawgs59klwf
   foreign key (item_cost_id)
   references item_cost;

alter table if exists purchase_order_line
   add constraint FK210t80fsgdi4s4g7tlg9vdgkd
   foreign key (purchase_order_id)
   references purchase_order;

alter table if exists sales_order
   add constraint FKqqe3xj99rblvm5n0h0cp48gsa
   foreign key (customer_id)
   references customer;

alter table if exists sales_order_line
   add constraint FKsx78nrsmy3oc216vk4ktmy1l9
   foreign key (item_id)
   references item;

alter table if exists sales_order_line
   add constraint FKbx4quc9tla4qypjygv8rghe02
   foreign key (item_price_id)
   references item_price;

alter table if exists sales_order_line
   add constraint FK3hhrmxpwt1cqlnxmtiwpy9wpg
   foreign key (sales_order_id)
   references sales_order;

alter table if exists stock
   add constraint FKf3gqo6e3aged60kygtbmtsd8s
   foreign key (item_id)
   references item;

alter table if exists stock
   add constraint FKhfylq2kdxj0ql2b7km0udxty6
   foreign key (warehouse_section_id)
   references warehouse_section;

alter table if exists warehouse_section
   add constraint FK2tnf3b2fjk6uoewms39i41j2k
   foreign key (warehouse_id)
   references warehouse;
