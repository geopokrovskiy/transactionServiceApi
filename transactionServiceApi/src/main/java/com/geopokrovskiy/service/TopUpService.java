package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.TopUpRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class TopUpService {

    private final TopUpRepository topUpRepository;

    private final PaymentRequestService paymentRequestService;

    private final WalletService walletService;

    @Transactional
    public TopUpRequestEntity addNewTopUp(TopUpRequestEntity topUpRequestEntity, UUID userId) {
        WalletEntity walletEntity = walletService.getWalletEntityById(topUpRequestEntity.getWalletId());
        if (walletEntity == null) {
            log.error("Wallet not found");
            throw new RuntimeException("Wallet with given id not found");
        }
        try {
            PaymentRequestEntity paymentRequestToSave = new PaymentRequestEntity().toBuilder()
                    .amount(topUpRequestEntity.getAmount())
                    .comment(topUpRequestEntity.getComment())
                    .paymentMethodId(topUpRequestEntity.getPaymentMethodId())
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .walletId(walletEntity.getUid())
                    .status(Status.ACTIVE)
                    .build();
            PaymentRequestEntity savedPaymentRequest = paymentRequestService.addNewPaymentRequest(paymentRequestToSave);
            log.info("Saved PaymentRequestEntity Top Up: {}", savedPaymentRequest);

            TopUpRequestEntity topUpToSave = new TopUpRequestEntity().toBuilder()
                    .provider(topUpRequestEntity.getProvider())
                    .createdAt(savedPaymentRequest.getCreatedAt())
                    .modifiedAt(savedPaymentRequest.getModifiedAt())
                    .comment(savedPaymentRequest.getComment())
                    .paymentRequestId(savedPaymentRequest.getUid())
                    .paymentMethodId(savedPaymentRequest.getPaymentMethodId())
                    .amount(savedPaymentRequest.getAmount())
                    .walletId(walletEntity.getUid())
                    .status(Status.ACTIVE)
                    .userId(userId)
                    .build();
            TopUpRequestEntity savedTopUp = topUpRepository.save(topUpToSave);
            ShardContextHolder.clearShard();
            return savedTopUp;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public TopUpRequestEntity getTopUpById(UUID topUpId) {
        return topUpRepository.findById(topUpId).orElse(null);
    }
}
