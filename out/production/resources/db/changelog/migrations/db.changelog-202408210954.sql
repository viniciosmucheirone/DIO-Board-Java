--liquibase formatted sql
--changeset junior:202408191938
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS(
                               id BIGINT IDENTITY(1,1) PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
    [order] INT NOT NULL,  -- Usando colchetes para "order", que é uma palavra reservada no SQL Server
                               kind VARCHAR(7) NOT NULL,
                               board_id BIGINT NOT NULL,
                               CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
                               CONSTRAINT unique_board_id_order UNIQUE (board_id, [order])  -- Definindo a chave única corretamente
);

--rollback DROP TABLE BOARDS_COLUMNS

