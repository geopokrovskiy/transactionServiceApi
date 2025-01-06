package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.transaction.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
