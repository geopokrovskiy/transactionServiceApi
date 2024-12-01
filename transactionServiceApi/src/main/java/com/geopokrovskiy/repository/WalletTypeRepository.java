package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTypeRepository extends JpaRepository<WalletTypeEntity, UUID> {
    public WalletTypeEntity findByUid(UUID uid);

    public WalletTypeEntity findByName(String name);

    public WalletTypeEntity findByCurrencyCode(String currencyCode);
}
