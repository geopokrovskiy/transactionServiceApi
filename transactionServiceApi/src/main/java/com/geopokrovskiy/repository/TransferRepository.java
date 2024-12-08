package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferRequestEntity, UUID> {
}
