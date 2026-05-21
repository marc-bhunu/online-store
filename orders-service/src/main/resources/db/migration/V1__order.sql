CREATE TABLE order_addresses
(
    id               UUID NOT NULL,
    type             SMALLINT,
    order_id         UUID,
    address_snapshot JSONB,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order_addresses PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id           UUID,
    order_id     UUID,
    product_id   UUID,
    product_name VARCHAR(255),
    price        BIGINT,
    quantity     INTEGER,
    subtotal     BIGINT,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE orders
(
    id           UUID NOT NULL,
    user_id      UUID,
    status       VARCHAR(255),
    total_amount BIGINT,
    currency     VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

ALTER TABLE order_addresses
    ADD CONSTRAINT uc_order_addresses_order UNIQUE (order_id);

ALTER TABLE order_addresses
    ADD CONSTRAINT FK_ORDER_ADDRESSES_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);