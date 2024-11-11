package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTypeRepository extends JpaRepository<WalletTypeEntity, UUID> {
}
