package com.geopokrovskiy.configuration;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
public class SpringBootIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer1 = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("root")
            .withReuse(true);

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer2 = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("root")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.shard1.url", postgreSQLContainer1::getJdbcUrl);
        registry.add("spring.datasource.shard1.username", postgreSQLContainer1::getUsername);
        registry.add("spring.datasource.shard1.password", postgreSQLContainer1::getPassword);

        registry.add("spring.datasource.shard2.url", postgreSQLContainer2::getJdbcUrl);
        registry.add("spring.datasource.shard2.username", postgreSQLContainer2::getUsername);
        registry.add("spring.datasource.shard2.password", postgreSQLContainer2::getPassword);
    }

}
