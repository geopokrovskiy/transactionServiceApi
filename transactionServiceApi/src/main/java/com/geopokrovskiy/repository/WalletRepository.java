package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.wallet.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
}
