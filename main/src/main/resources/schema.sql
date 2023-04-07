DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL UNIQUE,

    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) UNIQUE,

    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation        VARCHAR(2000)                           NOT NULL,
    category_id       BIGINT                                  NOT NULL,
    title             VARCHAR(120)                            NOT NULL,
    state             VARCHAR(20),
    description       VARCHAR(7000)                           NOT NULL,
    initiator_id      BIGINT,
    created_on        TIMESTAMP WITHOUT TIME ZONE,
    event_date        TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    published_on      TIMESTAMP WITHOUT TIME ZONE,
    lat               FLOAT                                   NOT NULL,
    lon               FLOAT                                   NOT NULL,
    is_paid           BOOLEAN,
    participant_limit BIGINT,
    is_moderation     BOOLEAN,
    views             BIGINT,

    CONSTRAINT pk_event PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES categories,
    FOREIGN KEY (initiator_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE,
    event_id     BIGINT,
    requester_id BIGINT,
    status       VARCHAR(20),

    CONSTRAINT pk_request PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES events,
    FOREIGN KEY (requester_id) REFERENCES users,
    UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN                                 NOT NULL,
    title  VARCHAR(255),

    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT REFERENCES compilations (id),
    event_id       BIGINT REFERENCES events (id),
    CONSTRAINT pk_compilation_event PRIMARY KEY (compilation_id, event_id)
);

