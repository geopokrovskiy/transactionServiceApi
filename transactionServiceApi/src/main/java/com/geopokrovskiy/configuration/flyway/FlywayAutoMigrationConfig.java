package com.geopokrovskiy.configuration.flyway;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayAutoMigrationConfig {

    @Value("${spring.datasource.shard1.username}")
    private String user;

    @Value("${spring.datasource.shard1.password}")
    private String password;

    @Bean
    @FlywayDataSource
    public DataSource shard1FlywayDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/transaction_service_api_shard1")
                .username(user)
                .password(password)
                .build();
    }

    @Bean
    @FlywayDataSource
    public DataSource shard2FlywayDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/transaction_service_api_shard2")
                .username(user)
                .password(password)
                .build();
    }

    @PostConstruct
    public void migrateAll() {
        Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/transaction_service_api_shard1", user, password)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/transaction_service_api_shard2", user, password)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}
