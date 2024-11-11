package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UnitTestUtils {

    public static List<WalletTypeEntity> getValidWalletTypes() {
        WalletTypeEntity walletTypeEntity1 = new WalletTypeEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestCoin")
                .currency_code("TCN")
                .status(Status.ACTIVE)
                .creator("test_user")
                .createdAt(LocalDateTime.now())
                .build();
        WalletTypeEntity walletTypeEntity2 = new WalletTypeEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("VirtualCoin")
                .currency_code("VTN")
                .status(Status.ACTIVE)
                .creator("test_user")
                .createdAt(LocalDateTime.now())
                .build();
        return List.of(walletTypeEntity1, walletTypeEntity2);
    }
}
