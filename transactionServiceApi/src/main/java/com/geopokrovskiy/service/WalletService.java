package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTypeService walletTypeService;

    @Transactional
    public WalletEntity addNewWallet(WalletEntity wallet, String currencyCode) {
        WalletTypeEntity walletType = walletTypeService.getWalletTypeByCurrencyCode(currencyCode);
        if (walletType != null && Status.ACTIVE == walletType.getStatus()) {
            log.info("A new request for creation of wallet with currency {} has been received for user {}.", walletType.getCurrencyCode(), wallet.getUserId());
            WalletEntity savedEntity = walletRepository.save(wallet.toBuilder()
                    .balance(0d)
                    .uid(UUID.randomUUID())
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .walletTypeId(walletType.getUid())
                    .status(Status.ACTIVE)
                    .build());
            ShardContextHolder.clearShard();
            return savedEntity;
        } else {
            log.warn("An incorrect POST request has been made by user {}.", wallet.getUserId());
            throw new IllegalArgumentException("Invalid wallet type");
        }
    }

    public WalletEntity getWalletEntityById(UUID walletId) {
        return walletRepository.findById(walletId).orElse(null);
    }

    public List<WalletEntity> getAllWalletsOfAUser(UUID userId) {
        log.info("A new request for retrieval of all wallets has been received for user {}.", userId);
        List<WalletEntity> retrievedEntities = walletRepository.findAll().stream().filter(wallet -> userId.equals(wallet.getUserId())
                && wallet.getStatus() == Status.ACTIVE).toList();
        ShardContextHolder.clearShard();
        return retrievedEntities;
    }

    public List<WalletEntity> getAllWalletsOfAUserWithCurrency(UUID userId, String currencyCode) {
        WalletTypeEntity walletType = walletTypeService.getWalletTypeByCurrencyCode(currencyCode);
        if (walletType != null && Status.ACTIVE == walletType.getStatus()) {
            log.info("A new request for retrieval of all wallets with currency {} has been received for user {}.", currencyCode, userId);
            List<WalletEntity> retrievedEntities = walletRepository.findAll().stream().filter(wallet -> userId.equals(wallet.getUserId())
                    && wallet.getStatus() == Status.ACTIVE
                    && currencyCode.equals(walletTypeService.getWalletTypeByUid(wallet.getWalletTypeId()).getCurrencyCode())).toList();
            ShardContextHolder.clearShard();
            return retrievedEntities;
        } else {
            log.warn("An incorrect GET request has been made by user {}.", userId);
            throw new IllegalArgumentException("Invalid wallet type");
        }
    }

    public WalletEntity addNewWallet(WalletEntity wallet) {
        wallet.setUid(wallet.getUid());
        return walletRepository.save(wallet);
    }

    public WalletEntity updateWalletBalance(WalletEntity wallet, double amount) {
        double actualBalance = wallet.getBalance();
        wallet.setBalance(actualBalance + amount);
        wallet.setModifiedAt(LocalDateTime.now());
        return walletRepository.save(wallet);
    }

}
