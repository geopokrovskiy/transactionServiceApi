package com.geopokrovskiy.unit;

import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.repository.WalletTypeRepository;
import com.geopokrovskiy.service.WalletTypeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WalletTypeServiceTest {
    @Mock
    private WalletTypeRepository walletTypeRepository;

    @InjectMocks
    private WalletTypeService walletTypeService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetWalletTypes() {
        List<WalletTypeEntity> expectedWalletTypes = UnitTestUtils.getValidWalletTypes();
        Mockito.when(walletTypeRepository.findAll()).thenReturn(expectedWalletTypes);

        List<WalletTypeEntity> actualWalletTypes = walletTypeService.getAllWalletTypes();
        Assert.assertEquals(expectedWalletTypes, actualWalletTypes);
    }
}
