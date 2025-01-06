package com.geopokrovskiy.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "transaction_service", name = "transactions")
@Entity
public class TransactionEntity {
    @Id
    @Column
    private UUID uid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_uid")
    private UUID userId;

    @Column(name = "wallet_uid")
    private UUID walletId;

    @Column(name = "wallet_name")
    private String walletName;

    @Column
    private Double amount;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column
    private TransactionState state;

    @Column(name = "payment_request_uid")
    private UUID paymentRequestId;
}
