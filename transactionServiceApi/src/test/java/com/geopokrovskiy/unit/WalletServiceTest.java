package com.geopokrovskiy.unit;

import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.WalletRepository;
import com.geopokrovskiy.service.WalletService;
import com.geopokrovskiy.service.WalletTypeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletTypeService walletTypeService;
    @InjectMocks
    private WalletService walletService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewWallet() {
        WalletEntity expectedWallet = UnitTestUtils.getValidWalletShard1_1();
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(expectedWallet);
        WalletEntity newWalletToAdd = UnitTestUtils.getValidWalletShard1_1WithoutId();
        WalletEntity actualWallet = walletService.addNewWallet(newWalletToAdd);

        assertEquals(expectedWallet, actualWallet);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));
    }
}
