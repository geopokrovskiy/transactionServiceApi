package com.geopokrovskiy.entity.wallet;

import com.geopokrovskiy.entity.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
@Entity
@Builder(toBuilder = true)
public class WalletEntity {
    @Id
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
    private Status status;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

  // private List<PaymentRequestEntity> paymentRequestsList;

  //  private List<TransactionEntity> transactionsList;
}
