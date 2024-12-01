package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.repository.WalletTypeRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Data
@Slf4j
public class WalletTypeService {
    private final WalletTypeRepository walletTypeRepository;

    public List<WalletTypeEntity> getAllWalletTypes() {
        List<WalletTypeEntity> walletTypesList = walletTypeRepository.findAll();
        log.info("The following wallet types have been retrieved: {}", walletTypesList);
        ShardContextHolder.clearShard();
        return walletTypesList;
    }

    public WalletTypeEntity getWalletTypeByUid(UUID uid) {
        WalletTypeEntity retrievedWalletType = walletTypeRepository.findByUid(uid);
        ShardContextHolder.clearShard();
        return retrievedWalletType;
    }

    public WalletTypeEntity getWalletTypeByCurrencyCode(String currencyCode) {
        WalletTypeEntity retrievedWalletType = walletTypeRepository.findByCurrencyCode(currencyCode);
        ShardContextHolder.clearShard();
        return retrievedWalletType;
    }

}
