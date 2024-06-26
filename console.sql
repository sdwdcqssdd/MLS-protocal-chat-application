CREATE TABLE "user"
(
    id       BIGSERIAL,
    name     VARCHAR(32) NOT NULL,
    password VARCHAR(32) NOT NULL,
    token    VARCHAR(32) NOT NULL,
    type     VARCHAR(32) NOT NULL,
    avatar   VARCHAR(256),
    nickname VARCHAR(255),
    email    VARCHAR(255),
    phone    VARCHAR(32),
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE chat
(
    id BIGSERIAL,
    PRIMARY KEY (id)
);

CREATE TABLE chat_member
(
    chat_id    BIGINT,
    user_id    BIGINT,
    read_until BIGINT DEFAULT 0,
    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    UNIQUE (chat_id, user_id)
);

CREATE TABLE chat_message
(
    chat_id   BIGINT,
    sender_id BIGINT,
    content   VARCHAR NOT NULL,
    time      BIGINT       NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (sender_id) REFERENCES "user" (id)
);




INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100000, 'admin', '12345678', '12345678901234567890123456789012', 'admin', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100001, 'user1', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100002, 'user2', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100003, 'user3', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100004, 'user4', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
