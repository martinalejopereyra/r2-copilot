package org.example.onboardingcopilot.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.*;

public class TestDatabaseInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String datasourceUrl = env.getProperty("spring.datasource.url",
                "jdbc:postgresql://localhost:5432/r2_onboarding_test");
        String username = env.getProperty("spring.datasource.username", "admin");
        String password = env.getProperty("spring.datasource.password", "password");
        String adminUrl = datasourceUrl.replaceAll("/[^/]+$", "/postgres");
        String dbName = datasourceUrl.substring(datasourceUrl.lastIndexOf("/") + 1);

        try (Connection conn = DriverManager.getConnection(adminUrl, username, password);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + dbName);
            }
        } catch (Exception e) {
            // already exists or will fail with clear error from Flyway
        }
    }
}