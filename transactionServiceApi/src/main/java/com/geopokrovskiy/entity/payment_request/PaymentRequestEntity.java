package com.geopokrovskiy.entity.payment_request;

import com.geopokrovskiy.entity.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Table(schema = "transaction_service.payment_requests")
public class PaymentRequestEntity {
    @Id
    private UUID uud;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_uid")
    private UUID userId;

    @Column
    private UUID walletId;

    @Column
    private Double amount;

    @Column
    private Status status;

    @Column
    private String comment;

    @Column
    private Long paymentMethodId;
}
