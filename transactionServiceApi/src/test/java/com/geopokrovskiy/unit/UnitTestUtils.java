package com.geopokrovskiy.unit;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UnitTestUtils {

    public static List<WalletTypeEntity> getValidWalletTypes() {
        WalletTypeEntity walletTypeEntity1 = new WalletTypeEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestCoin")
                .currencyCode("TCN")
                .status(Status.ACTIVE)
                .creator("test_user")
                .createdAt(LocalDateTime.now())
                .build();
        WalletTypeEntity walletTypeEntity2 = new WalletTypeEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("VirtualCoin")
                .currencyCode("VTN")
                .status(Status.ACTIVE)
                .creator("test_user")
                .createdAt(LocalDateTime.now())
                .build();
        return List.of(walletTypeEntity1, walletTypeEntity2);
    }

    public static WalletTypeEntity getValidWalletType() {
        return new WalletTypeEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestCoin")
                .currencyCode("TCN")
                .status(Status.ACTIVE)
                .creator("test_user")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static WalletEntity getValidWalletShard1_1WithoutId() {
        return new WalletEntity().toBuilder()
                .name("TestWallet")
                .userId(UUID.fromString("21ec3a64-dacd-4f1f-9948-4bcef40da43d"))
                .balance(42.0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .walletTypeId(getValidWalletType().getUid())
                .status(Status.ACTIVE)
                .build();
    }

    public static WalletEntity getValidWalletShard1_1() {
        return new WalletEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestWallet")
                .userId(UUID.fromString("21ec3a64-dacd-4f1f-9948-4bcef40da43d"))
                .balance(42.0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .walletTypeId(getValidWalletType().getUid())
                .status(Status.ACTIVE)
                .build();
    }

    public static WalletEntity getValidWalletShard1_2() {
        return new WalletEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestWallet")
                .userId(UUID.fromString("03ac3a76-dabd-1f1f-9918-4bcef40da43b"))
                .balance(42.0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .walletTypeId(getValidWalletType().getUid())
                .status(Status.ACTIVE)
                .build();
    }

    public static WalletEntity getValidWalletShard2() {
        return new WalletEntity().toBuilder()
                .uid(UUID.randomUUID())
                .name("TestWallet")
                .userId(UUID.fromString("63ec3a66-dabd-1f1f-9918-4bcef40da43a"))
                .balance(17.0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .walletTypeId(getValidWalletType().getUid())
                .status(Status.ACTIVE)
                .build();
    }

    public static PaymentRequestEntity getValidPaymentRequestShard1_1() {
        WalletEntity walletEntity1 = getValidWalletShard1_1();

        return new PaymentRequestEntity().toBuilder()
                .uid(UUID.randomUUID())
                .walletId(walletEntity1.getUid())
                .comment("Test comment1_1")
                .paymentMethodId(42L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .amount(37.0)
                .status(Status.ACTIVE)
                .userId(walletEntity1.getUserId())
                .build();
    }

    public static PaymentRequestEntity getValidPaymentRequestShard1_2() {
        WalletEntity walletEntity1 = getValidWalletShard1_2();

        return new PaymentRequestEntity().toBuilder()
                .uid(UUID.randomUUID())
                .walletId(walletEntity1.getUid())
                .comment("Test comment1_2")
                .paymentMethodId(42L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .amount(37.0)
                .status(Status.ACTIVE)
                .userId(walletEntity1.getUserId())
                .build();
    }

    public static PaymentRequestEntity getValidPaymentRequestShard2_1() {
        WalletEntity walletEntity2 = getValidWalletShard2();

        return new PaymentRequestEntity().toBuilder()
                .uid(UUID.randomUUID())
                .walletId(walletEntity2.getUid())
                .comment("Test comment2")
                .paymentMethodId(42L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .amount(29.0)
                .status(Status.ACTIVE)
                .userId(walletEntity2.getUserId())
                .build();
    }

    public static TransferRequestEntity getValidTransferRequestOneShard_withoutId() {

        WalletEntity walletEntityFrom = getValidWalletShard1_1();
        WalletEntity walletEntityTo = getValidWalletShard1_2();

        return new TransferRequestEntity()
                .toBuilder()
                .walletFromId(walletEntityFrom.getUid())
                .walletToId(getValidWalletShard1_2().getUid())
                .userFromId(walletEntityTo.getUserId())
                .userToId(walletEntityTo.getUserId())
                .paymentRequestIdFrom(getValidPaymentRequestShard1_1().getUid())
                .paymentRequestIdTo(getValidPaymentRequestShard1_2().getUid())
                .status(Status.ACTIVE)
                .systemRate("1.0")
                .build();
    }

}
