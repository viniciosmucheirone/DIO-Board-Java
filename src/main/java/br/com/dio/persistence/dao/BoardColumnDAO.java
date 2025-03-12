package br.com.dio.persistence.dao;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.findByName;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, [order], kind, board_id) VALUES (?, ?, ?, ?);";
        try (var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();

            // Recuperar o ID gerado após o INSERT
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));  // O primeiro campo é o 'id' gerado
                }
            }
            return entity;
        }
    }
    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException{
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, [order], kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY [order]";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql =
                """
                SELECT BOARDS_COLUMNS.id,
                       BOARDS_COLUMNS.name,
                       BOARDS_COLUMNS.kind,
                       (SELECT COUNT(CARDS.id)
                           FROM CARDS
                           WHERE CARDS.board_column_id = BOARDS_COLUMNS.id) AS cards_amount
                FROM BOARDS_COLUMNS
                WHERE board_id = ?
                ORDER BY [order];
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("BOARDS_COLUMNS.id"),
                        resultSet.getString("BOARDS_COLUMNS.name"),
                        findByName(resultSet.getString("BOARDS_COLUMNS.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }


    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql =
                """
                SELECT BOARDS_COLUMNS.name,
                       BOARDS_COLUMNS.kind,
                       CARDS.id,
                       CARDS.title,
                       CARDS.description
                  FROM BOARDS_COLUMNS
                  LEFT JOIN CARDS
                    ON CARDS.board_column_id = BOARDS_COLUMNS.id
                 WHERE BOARDS_COLUMNS.id = ?;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("BOARDS_COLUMNS.name"));
                entity.setKind(findByName(resultSet.getString("BOARDS_COLUMNS.kind")));
                do {
                    var card = new CardEntity();
                    if (isNull(resultSet.getString("CARDS.title"))) {
                        break;
                    }
                    card.setId(resultSet.getLong("CARDS.id"));
                    card.setTitle(resultSet.getString("CARDS.title"));
                    card.setDescription(resultSet.getString("CARDS.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }


}
