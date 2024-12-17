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
@Table(schema = "transaction_service", name = "transfer_requests")
public class TransferRequestEntity {
    @Id
    @Column(name = "uid")
    private UUID uid;

    @Column(name = "payment_request_uid_from")
    private UUID paymentRequestIdFrom;

    @Column(name = "payment_request_uid_to")
    private UUID paymentRequestIdTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "system_rate")
    private String systemRate;

    @Transient
    private Double amount;

    @Transient
    private String comment;

    @Transient
    private long paymentMethodId;

    @Transient
    private UUID walletFromId;

    @Transient
    private UUID walletToId;

    @Transient
    private UUID userFromId;

    @Transient
    private UUID userToId;

    @Transient
    private LocalDateTime modifiedAt;

    @Transient
    private Status status;

}
