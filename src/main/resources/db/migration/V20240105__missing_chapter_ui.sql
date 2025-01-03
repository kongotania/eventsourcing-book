create table cart_items_read_model_entity
(
    price           float(53),
    total_price     float(53),
    aggregate_id    uuid not null,
    item_id         uuid not null,
    description     varchar(255),
    image           varchar(255),
    product_id      varchar(255),
    sequence_number NUMERIC,
    primary key (aggregate_id, item_id)
);

create table cart_items_projection_version
(
    sequence_number bigint,
    aggregate_id    uuid not null,
    primary key (aggregate_id)
);

create table inventory_projection_version
(
    sequence_number bigint,
    aggregate_id    uuid not null,
    primary key (aggregate_id)
)
