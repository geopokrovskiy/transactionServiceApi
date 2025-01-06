package com.geopokrovskiy.it;

import com.geopokrovskiy.configuration.SpringBootIntegrationTest;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.repository.WalletTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class WalletTypeControllerTest extends SpringBootIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private WalletTypeRepository walletTypeRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        walletTypeRepository.deleteAll();
        walletTypeRepository.saveAll(List.of(
                new WalletTypeEntity().toBuilder()
                        .uid(UUID.randomUUID())
                        .name("Test coin")
                        .createdAt(LocalDateTime.now())
                        .creator("geopokrovskiy")
                        .currencyCode("TCN")
                        .status(Status.ACTIVE).build(),
                new WalletTypeEntity().toBuilder()
                        .uid(UUID.randomUUID())
                        .name("Virtual coin")
                        .createdAt(LocalDateTime.now())
                        .creator("geopokrovskiy")
                        .currencyCode("VCN")
                        .status(Status.ACTIVE).build()));
    }

    @Test
    @WithMockUser
    public void getAllWalletTypes_shouldReturnWalletTypes() throws Exception {
        mockMvc.perform(get("/api/v1/wallet_types/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test coin"))
                .andExpect(jsonPath("$[1].name").value("Virtual coin"));
    }
}
