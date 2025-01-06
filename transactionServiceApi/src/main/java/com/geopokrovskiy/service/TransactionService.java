package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.entity.transaction.TransactionType;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TopUpService topUpService;
    private final PaymentRequestService paymentRequestService;
    private final WalletService walletService;

    public TransactionEntity createNewTopUpTransaction(UUID topUpRequestId, UUID userId) {
        TopUpRequestEntity topUpRequestEntity = topUpService.getTopUpById(topUpRequestId);
        if (topUpRequestEntity == null) {
            log.error("Top up request not found");
            throw new RuntimeException("Top up request not found");
        }

        PaymentRequestEntity paymentRequestEntity = paymentRequestService.getPaymentRequestById(topUpRequestEntity.getPaymentRequestId());
        if (!paymentRequestEntity.getUserId().equals(userId)) {
            log.error("Top up request user id mismatch");
            throw new RuntimeException("Top up request user id mismatch");
        }

        WalletEntity wallet = walletService.getWalletEntityById(paymentRequestEntity.getWalletId());

        TransactionEntity transaction = new TransactionEntity().toBuilder()
                .uid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .userId(paymentRequestEntity.getUserId())
                .walletId(wallet.getUid())
                .paymentRequestId(topUpRequestEntity.getPaymentRequestId())
                .updatedAt(LocalDateTime.now())
                .amount(paymentRequestEntity.getAmount())
                .transactionType(TransactionType.TOP_UP)
                .state(TransactionState.CREATED)
                .walletName(wallet.getName())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created: {}", savedTransaction);
        return savedTransaction;
    }

    public TransactionEntity finalizeTopUpTransaction(UUID transactionId, TransactionState state, UUID userId) {
        TransactionEntity transactionInProgress = transactionRepository.findById(transactionId).orElse(null);
        if (transactionInProgress == null) {
            log.error("Top up transaction not found");
            throw new RuntimeException("Top up transaction not found");
        }

        if (!transactionInProgress.getState().equals(TransactionState.CREATED)) {
            log.error("Top up transaction not created");
            throw new RuntimeException("Top up transaction not created");
        }

        if (!transactionInProgress.getUserId().equals(userId)) {
            log.error("Top up request user id mismatch");
            throw new RuntimeException("Top up request user id mismatch");
        }

        transactionInProgress.setUpdatedAt(LocalDateTime.now());
        transactionInProgress.setState(TransactionState.IN_PROGRESS);

        TransactionEntity savedTransactionInProgress = transactionRepository.save(transactionInProgress);
        log.info("Transaction in progress {}", savedTransactionInProgress);

        savedTransactionInProgress.setState(state);
        TransactionEntity savedFinalizedTransaction = transactionRepository.save(savedTransactionInProgress);
        log.info("Transaction finalized {}", savedFinalizedTransaction);
        return savedFinalizedTransaction;
    }

}
