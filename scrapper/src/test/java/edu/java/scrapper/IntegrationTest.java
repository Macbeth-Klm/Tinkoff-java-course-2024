package edu.java.scrapper;

import java.io.File;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.Duration;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres")
            .waitingFor((
                Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofSeconds(30))
            ));
        POSTGRES.start();

        runMigrations(POSTGRES);
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
        Path changeLogPath = new File("").toPath().toAbsolutePath()
            .getParent().resolve("migrations");
        try (
            Liquibase liquibase = new Liquibase(
                "master.xml",
                new DirectoryResourceAccessor(changeLogPath),
                new JdbcConnection(DriverManager.getConnection(
                    c.getJdbcUrl(),
                    c.getUsername(),
                    c.getPassword()
                ))
            )
        ) {
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
