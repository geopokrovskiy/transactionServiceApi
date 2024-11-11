package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.repository.WalletTypeRepository;
import com.geopokrovskiy.service.WalletTypeService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WalletTypeServiceTest {
    private final WalletTypeRepository walletTypeRepository = Mockito.mock(WalletTypeRepository.class);

    @InjectMocks
    private WalletTypeService walletTypeService;

    @Test
    public void testGetWalletTypes() {
        List<WalletTypeEntity> expectedWalletTypes = UnitTestUtils.getValidWalletTypes();
        Mockito.when(walletTypeRepository.findAll()).thenReturn(expectedWalletTypes);

        List<WalletTypeEntity> actualWalletTypes = walletTypeService.getAllWalletTypes();
        Assert.assertEquals(expectedWalletTypes, actualWalletTypes);
    }
}
