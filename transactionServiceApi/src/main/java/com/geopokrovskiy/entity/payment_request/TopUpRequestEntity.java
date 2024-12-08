package com.geopokrovskiy.entity.payment_request;

import com.geopokrovskiy.entity.status.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(schema = "transaction_service", name = "top_up_requests")
public class TopUpRequestEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uid;

    @Column(name = "payment_request_uid")
    private UUID paymentRequestId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private String provider;

    @Transient
    private Double amount;

    @Transient
    private String comment;

    @Transient
    private long paymentMethodId;

    @Transient
    private UUID walletId;

    @Transient
    private LocalDateTime modifiedAt;

    @Transient
    private Status status;

    @Transient
    private UUID userId;
}

