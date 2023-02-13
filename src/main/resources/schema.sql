DROP TABLE if exists access_key cascade;
DROP TABLE if exists address cascade;
DROP TABLE if exists category cascade;
DROP TABLE if exists client cascade;
DROP TABLE if exists order_product cascade;
DROP TABLE if exists orders cascade;
DROP TABLE if exists product cascade;
DROP TABLE if exists role cascade;
DROP TABLE if exists role_permission cascade;
DROP TABLE if exists users cascade;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table public.category
(
    id          bigserial primary key,
    active      boolean                not null,
    added_at    timestamp default now(),
    deleted     boolean                not null,
    description character varying(255),
    name        character varying(255) not null unique
);

create table public.client
(
    id           bigserial primary key,
    phone_number character varying(255) unique,
    username     character varying(255)
);

create table public.access_key
(
    id         bigserial primary key,
    access     character varying(255),
    deleted    boolean not null,
    valid_till timestamp(6) without time zone,
    client_id  bigint references public.client
);


create table public.role
(
    id          bigserial primary key,
    active      boolean not null default true,
    added_at    timestamp        default now(),
    deleted     boolean not null default false,
    description character varying(255),
    name        character varying(255) unique
);

create table public.role_permission
(
    role_id    integer                not null,
    permission character varying(255) not null,
    primary key (role_id, permission),
    foreign key (role_id) references public.role (id)
        match simple on update no action on delete no action
);

create table public.users
(
    id           uuid primary key       not null default gen_random_uuid(),
    active       boolean                not null,
    added_at     timestamp                       default now(),
    deleted      boolean                not null default false,
    enabled      boolean                not null default true,
    first_name   character varying(255),
    last_name    character varying(255),
    password     character varying(255) not null,
    phone_number character varying(255) not null unique,
    role_id      integer references public.role
);


create table public.address
(
    id           bigserial primary key,
    city         character varying(255),
    deleted      boolean not null,
    house_number integer,
    street       character varying(255),
    user_id      uuid    not null references public.users
);


create table public.orders
(
    id            bigserial primary key,
    active        boolean not null,
    added_at      timestamp        default now(),
    deleted       boolean not null default false,
    overall_price double precision,
    status        character varying(255),
    updated_by_id uuid,
    address_id    bigint,
    user_id       uuid    not null references public.users
);

create table public.product
(
    id          bigserial primary key,
    active      boolean                not null default true,
    added_at    timestamp                       default now(),
    deleted     boolean                not null default false,
    description character varying(255),
    name        character varying(255) not null,
    price       double precision,
    category_id bigint                 not null references public.category
);


create table public.order_product
(
    id         bigserial primary key,
    deleted    boolean not null,
    price      double precision,
    quantity   integer,
    order_id   bigint  not null references public.orders,
    product_id bigint  not null references public.product
);


CREATE OR REPLACE FUNCTION delete_order_product_order_deleted()
    RETURNS TRIGGER
AS
$$
BEGIN
    IF NEW.deleted = true THEN
        update order_product set deleted = true where order_id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER delete_order_product_order_deleted_trigger
    BEFORE UPDATE
    ON orders
    FOR EACH ROW
EXECUTE PROCEDURE delete_order_product_order_deleted();


CREATE OR REPLACE FUNCTION delete_address_user_deleted()
    RETURNS TRIGGER
AS
$$
BEGIN
    IF NEW.deleted = true THEN
        update address set deleted = true where user_id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ language plpgsql;

CREATE TRIGGER delete_address_user_deleted_trigger
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE PROCEDURE delete_address_user_deleted();


CREATE OR REPLACE FUNCTION delete_products_category_deleted()
    RETURNS TRIGGER
AS
$$
BEGIN
    IF NEW.deleted = true THEN
        update product set deleted = true where category_id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER delete_products_category_deleted_trigger
    BEFORE UPDATE
    ON category
    FOR EACH ROW
EXECUTE PROCEDURE delete_products_category_deleted();

