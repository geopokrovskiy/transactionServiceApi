package com.geopokrovskiy.entity.payment_request;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Table(schema = "transaction_service.withdrawal_requests")
public class WithdrawalRequestEntity {
    @Column
    private UUID uid;

    @Column(name = "payment_request_id")
    private UUID paymentRequestId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
