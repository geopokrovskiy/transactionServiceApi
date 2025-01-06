package com.geopokrovskiy.it;

import com.geopokrovskiy.configuration.SpringBootIntegrationTest;
import com.geopokrovskiy.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class WalletControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        walletRepository.deleteAll();
    }
}
