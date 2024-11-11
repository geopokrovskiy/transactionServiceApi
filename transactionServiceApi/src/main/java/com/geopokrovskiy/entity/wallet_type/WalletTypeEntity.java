package com.geopokrovskiy.entity.wallet_type;

import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.user.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(schema = "transaction_service", name = "wallet_types")
public class WalletTypeEntity {

    @Id
    private UUID uid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "name")
    private String name;

    @Column(name = "currency_code")
    private String currency_code;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column
    private String creator;

    @Column
    private String modifier;

    //   private List<WalletEntity> walletEntityList;


}
