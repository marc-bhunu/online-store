CREATE TABLE payment_items
(
    id         UUID NOT NULL,
    payment_id UUID,
    amount     BIGINT,
    quantity   BIGINT,
    name       VARCHAR(255),
    currency   VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_payment_items PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id              UUID NOT NULL,
    order_id        UUID,
    user_id         UUID,
    idempotency_key VARCHAR(255),
    session_id      VARCHAR(255),
    total_amount    BIGINT,
    currency        VARCHAR(255),
    status          VARCHAR(255),
    payment_method  VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

ALTER TABLE payment_items
    ADD CONSTRAINT FK_PAYMENT_ITEMS_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payments (id);