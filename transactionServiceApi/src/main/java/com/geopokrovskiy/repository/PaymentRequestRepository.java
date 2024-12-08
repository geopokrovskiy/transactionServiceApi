package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequestEntity, UUID> {
}
