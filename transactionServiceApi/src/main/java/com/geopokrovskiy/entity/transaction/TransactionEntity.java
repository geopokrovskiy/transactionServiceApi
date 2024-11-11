package com.geopokrovskiy.entity.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Table(schema = "transaction_service.transaction")
public class TransactionEntity {
    @Id
    private UUID uid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_uid")
    private UUID userId;

    @Column
    private UUID walletId;

    @Column
    private String walletName;

    @Column
    private Double amount;

    @Column
    private TransactionType transactionType;

    @Column
    private TransactionState state;

    @Column
    private UUID paymentRequestId;
}
