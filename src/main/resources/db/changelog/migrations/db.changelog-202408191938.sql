--liquibase formatted sql
--changeset junior:202408191938
--comment: boards table create

CREATE TABLE BOARDS (
        id BIGINT PRIMARY KEY,  -- Usando IDENTITY ao invés de AUTO_INCREMENT
        name VARCHAR(255) NOT NULL
);

--rollback DROP TABLE BOARDS;