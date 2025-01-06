CREATE SCHEMA IF NOT EXISTS transaction_service;

create table IF not exists transaction_service.wallet_types
(
    uid           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at    timestamp default now() not null,
    modified_at   timestamp,
    name          varchar(32)             not null,
    currency_code varchar(3)              not null,
    status        varchar(18)             not null,
    archived_at   timestamp,
    user_type     varchar(15),
    creator       varchar(255),
    modifier      varchar(255)
    );

create table IF not exists transaction_service.wallets
(
    uid             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at      timestamp default now() not null,
    modified_at     timestamp,
    name            varchar(32)             not null,
    wallet_type_uid uuid                    not null
    constraint fk_wallets_wallet_types references transaction_service.wallet_types(uid),
    user_uid        uuid                    not null,
    status          varchar(30)             not null,
    balance         decimal   default 0.0   not null,
    archived_at     timestamp
    );

create table IF not exists transaction_service.payment_requests
(
    uid               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at        timestamp default now()              not null,
    modified_at       timestamp,
    user_uid          uuid                                 not null,
    wallet_uid        uuid                                 not null references transaction_service.wallets(uid),
    amount            decimal   default 0.0                not null,
    status            varchar,
    comment           varchar(256),
    payment_method_id bigint
    );

create table IF not exists transaction_service.transactions
(
    uid                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at          timestamp default now()              not null,
    modified_at         timestamp,
    user_uid            uuid                                 not null,
    wallet_uid          uuid                                 not null references transaction_service.wallets (uid),
    wallet_name         varchar(32)                          not null,
    amount              decimal   default 0.0                not null,
    type                varchar(32)                          not null,
    state               varchar(32)                          not null,
    payment_request_uid uuid                                 not null references transaction_service.payment_requests on delete cascade
    );

create table IF not exists transaction_service.top_up_requests
(
    uid                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at          timestamp default now()              not null,
    provider            varchar                              not null,
    payment_request_uid uuid                                 not null references transaction_service.payment_requests on delete cascade
    );

create table IF not exists transaction_service.withdrawal_requests
(
    uid                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at          timestamp default now()              not null,
    payment_request_uid uuid                                 not null references transaction_service.payment_requests on delete cascade
    );

create table IF not exists transaction_service.transfer_requests
(
    uid                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at               timestamp default now()              not null,
    system_rate              varchar                              not null,
    payment_request_uid_from uuid                                 not null references transaction_service.payment_requests on delete cascade,
    payment_request_uid_to   uuid                                 not null references transaction_service.payment_requests on delete cascade
    );
