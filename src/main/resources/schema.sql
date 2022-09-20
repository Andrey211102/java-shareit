CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR(50)  NOT NULL,
    email   VARCHAR(100) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT un_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(200) DEFAULT '',
    available   BOOLEAN      DEFAULT true,
    owner_id    BIGINT,

    CONSTRAINT pk_items PRIMARY KEY (item_id),
    CONSTRAINT fk_items_owner_id FOREIGN KEY (owner_id) REFERENCES users (user_id)
);
CREATE TABLE IF NOT EXISTS bookings
(
    booking_id    BIGINT GENERATED ALWAYS AS IDENTITY,
    item_id       BIGINT,
    status        VARCHAR(25),
    booker_id     BIGINT,
    start_booking TIMESTAMP NOT NULL,
    end_booking   TIMESTAMP NOT NULL,

    CONSTRAINT pk_bookings PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS item_comments
(
    comment_id   BIGINT GENERATED ALWAYS AS IDENTITY,
    item_id      BIGINT,
    author_id    BIGINT,
    comment_text VARCHAR(300),
    created      TIMESTAMP NOT NULL,

    CONSTRAINT pk_item_comments PRIMARY KEY (comment_id),
    CONSTRAINT fk_item_comments_item_id FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_item_comments_booker_id FOREIGN KEY (author_id) REFERENCES users (user_id)
);