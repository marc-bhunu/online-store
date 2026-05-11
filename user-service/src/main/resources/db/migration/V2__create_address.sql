CREATE TABLE address(
    id uuid primary key not null,
    user_id uuid not null,
    type varchar(255) not null default 'HOME',
    line1 varchar(255) not null,
    line2 varchar(255),
    city varchar(255) not null,
    state varchar(255) not null,
    country varchar(255) not null,
    zip varchar(255) not null default '00000',
    is_default boolean not null default false,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    constraint fk_user_id foreign key (user_id) references _user(id) on delete cascade
);
CREATE INDEX idx_address_user_id ON address(user_id);