package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitDatabaseTest extends IntegrationTest {
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(
        DataSourceBuilder
            .create()
            .url(POSTGRES.getJdbcUrl())
            .username(POSTGRES.getUsername())
            .password(POSTGRES.getPassword())
            .build()
    );

    @Test
    public void shouldConfirmDatabaseCreatingAndLaunch() {
        assertTrue(POSTGRES.isCreated() && POSTGRES.isRunning());
    }

    @Test
    public void shouldConfirmDatabaseDatabaseTablesCreating() {
        Integer chatTableEntriesCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_name = ?",
            Integer.class,
            "chat"
        );
        assert (chatTableEntriesCount != null);

        Integer linkTableEntriesCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_name = ?",
            Integer.class,
            "link"
        );
        assert (linkTableEntriesCount != null);

        Integer chatLinkTableEntriesCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_name = ?",
            Integer.class,
            "chat_link"
        );
        assert (chatLinkTableEntriesCount != null);

        assertAll(
            () -> assertEquals(1, (int) chatTableEntriesCount),
            () -> assertEquals(1, (int) linkTableEntriesCount),
            () -> assertEquals(1, (int) chatLinkTableEntriesCount)
        );
    }
}
