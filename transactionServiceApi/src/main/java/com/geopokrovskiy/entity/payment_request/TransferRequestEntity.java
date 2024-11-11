package com.geopokrovskiy.entity.payment_request;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Table(schema = "transaction_service.transfer_request")
public class TransferRequestEntity {
    @Column
    private UUID uid;

    @Column(name = "payment_request_id_from")
    private UUID paymentRequestIdFrom;

    @Column(name = "payment_request_id_to")
    private UUID paymentRequestIdTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private String systemRate;
}
