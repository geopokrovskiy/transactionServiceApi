package com.geopokrovskiy.entity.wallet;

import com.geopokrovskiy.entity.status.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "transaction_service", name = "wallets",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "wallet_type_uid"})})

@Entity
@Builder(toBuilder = true)
public class WalletEntity {
    @Id
    @Column(name = "uid")
    private UUID uid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "name")
    private String name;

    @Column(name = "wallet_type_uid")
    private UUID walletTypeId;

    @Column(name = "user_uid")
    private UUID userId;

    @Column
    private Double balance;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    // private List<PaymentRequestEntity> paymentRequestsList;

    //  private List<TransactionEntity> transactionsList;
}
