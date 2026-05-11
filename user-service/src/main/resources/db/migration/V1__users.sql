CREATE TABLE _user(
    id UUID primary key,
    email varchar(255) unique not null,
    username varchar(255) not null unique,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    phone varchar(255) not null,
    status varchar(255) default 'ACTIVE',
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);
CREATE INDEX idx_user_email ON _user(email);
CREATE INDEX idx_user_username ON _user(username);