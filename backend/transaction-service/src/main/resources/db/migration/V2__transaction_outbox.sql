CREATE TABLE IF NOT EXISTS transaction_outbox (
    id bigserial PRIMARY KEY,
    transaction_reference varchar(30) NOT NULL UNIQUE,
    customer_number varchar(20) NOT NULL,
    source_account_number varchar(30),
    destination_account_number varchar(30),
    type varchar(30) NOT NULL,
    amount numeric(19,2) NOT NULL,
    currency varchar(3) NOT NULL,
    description varchar(255),
    status varchar(20) NOT NULL,
    created_at timestamp NOT NULL,
    sent_at timestamp
);
CREATE INDEX IF NOT EXISTS idx_outbox_status ON transaction_outbox(status);