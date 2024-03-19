package edu.java.configuration;

import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DSLContextConfig {
    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(
            dataSource,
            SQLDialect.POSTGRES,
            new Settings().withRenderSchema(false)
                .withRenderFormatted(true)
                .withRenderQuotedNames(RenderQuotedNames.NEVER)
        );
    }
}
