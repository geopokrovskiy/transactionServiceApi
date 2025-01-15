package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.WithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;
    private final PaymentRequestService paymentRequestService;
    private final WalletService walletService;

    @Transactional
    public WithdrawalRequestEntity addNewWithdrawal(WithdrawalRequestEntity withdrawalRequestEntity, UUID userId) {
        WalletEntity walletEntity = walletService.getWalletEntityById(withdrawalRequestEntity.getWalletId());
        if (walletEntity == null) {
            log.error("Wallet not found");
            throw new RuntimeException("Wallet with given id not found");
        }
        try {
            PaymentRequestEntity paymentRequestToSave = new PaymentRequestEntity().toBuilder()
                    .amount((-1) * withdrawalRequestEntity.getAmount())
                    .comment(withdrawalRequestEntity.getComment())
                    .paymentMethodId(withdrawalRequestEntity.getPaymentMethodId())
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .walletId(walletEntity.getUid())
                    .status(Status.ACTIVE)
                    .build();
            PaymentRequestEntity savedPaymentRequest = paymentRequestService.addNewPaymentRequest(paymentRequestToSave);
            log.info("Saved PaymentRequestEntity Withdrawal: {}", savedPaymentRequest);

            WithdrawalRequestEntity withdrawalEntityToSave = new WithdrawalRequestEntity().toBuilder()
                    .createdAt(savedPaymentRequest.getCreatedAt())
                    .modifiedAt(savedPaymentRequest.getModifiedAt())
                    .comment(savedPaymentRequest.getComment())
                    .paymentRequestId(savedPaymentRequest.getUid())
                    .paymentMethodId(savedPaymentRequest.getPaymentMethodId())
                    .amount((-1) * savedPaymentRequest.getAmount())
                    .walletId(walletEntity.getUid())
                    .status(Status.ACTIVE)
                    .userId(userId)
                    .build();
            WithdrawalRequestEntity savedWithdrawal = withdrawalRepository.save(withdrawalEntityToSave);
            ShardContextHolder.clearShard();
            return savedWithdrawal;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public WithdrawalRequestEntity getWithdrawalById(UUID id) {
        return withdrawalRepository.findById(id).orElse(null);
    }
}
