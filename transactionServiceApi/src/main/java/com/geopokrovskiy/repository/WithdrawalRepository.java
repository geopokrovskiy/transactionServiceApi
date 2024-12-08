package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WithdrawalRepository extends JpaRepository<WithdrawalRequestEntity, UUID> {
}
