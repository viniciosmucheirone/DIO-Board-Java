--liquibase formatted sql
--changeset junior:202408191938
--comment: blocks table create

CREATE TABLE BLOCKS(
                       id BIGINT IDENTITY(1,1) PRIMARY KEY,  -- Usando IDENTITY ao invés de AUTO_INCREMENT
                       blocked_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,  -- Usando DATETIME2 em vez de TIMESTAMP
                       block_reason VARCHAR(255) NOT NULL,
                       unblocked_at DATETIME2 NULL,  -- Usando DATETIME2 em vez de TIMESTAMP
                       unblock_reason VARCHAR(255) NOT NULL,
                       card_id BIGINT NOT NULL,
                       CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
);

--rollback DROP TABLE BLOCKS;
