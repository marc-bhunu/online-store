CREATE TABLE order_addresses
(
    id         UUID NOT NULL,
    type       VARCHAR(255),
    order_id   UUID,
    line1      VARCHAR(255),
    line2      VARCHAR(255),
    city       VARCHAR(255),
    state      VARCHAR(255),
    zip        VARCHAR(255),
    country    VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order_addresses PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id           UUID NOT NULL,
    order_id     UUID,
    product_id   UUID,
    product_name VARCHAR(255),
    price        BIGINT,
    quantity     INTEGER,
    subtotal     BIGINT,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
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