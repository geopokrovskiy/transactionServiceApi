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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(schema = "transaction_service", name = "payment_requests")
public class PaymentRequestEntity {
    @Id
    @Column(name = "uid")
    private UUID uid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "user_uid")
    private UUID userId;

    @Column(name = "wallet_uid")
    private UUID walletId;

    @Column
    private Double amount;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private String comment;

    @Column(name = "payment_method_id")
    private Long paymentMethodId;
}
