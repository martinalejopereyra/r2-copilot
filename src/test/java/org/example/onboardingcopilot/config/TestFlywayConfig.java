package org.example.onboardingcopilot.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@TestConfiguration
@Profile("test")
public class TestFlywayConfig {

    @Value("${spring.flyway.locations:classpath:db/migration,classpath:db/seed,classpath:db/testdata}")
    private String[] locations;

    @Bean
    @Primary
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(
                        "classpath:db/migration",
                        "classpath:db/seed",
                        "classpath:db/testdata"
                )
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}