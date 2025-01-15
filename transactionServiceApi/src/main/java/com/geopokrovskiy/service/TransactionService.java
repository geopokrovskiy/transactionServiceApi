package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.entity.transaction.TransactionType;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.TransactionRepository;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TopUpService topUpService;
    private final WithdrawalService withdrawalService;
    private final TransferService transferService;
    private final PaymentRequestService paymentRequestService;
    private final WalletService walletService;

    public TransactionEntity createNewTopUpTransaction(UUID topUpRequestId, UUID userId) {
        TopUpRequestEntity topUpRequestEntity = topUpService.getTopUpById(topUpRequestId);
        if (topUpRequestEntity == null) {
            log.error("Top up request not found");
            throw new RuntimeException("Top up request not found");
        }

        PaymentRequestEntity paymentRequestEntity = paymentRequestService.getPaymentRequestById(topUpRequestEntity.getPaymentRequestId());

        if (!paymentRequestEntity.getStatus().equals(Status.ACTIVE)) {
            log.error("Payment request status is not ACTIVE");
            throw new RuntimeException("Payment request status is not ACTIVE");
        }

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
        log.info("Top up transaction created: {}", savedTransaction);
        return savedTransaction;
    }

    @Transactional
    public TransactionEntity createNewWithdrawalTransaction(UUID withdrawalRequestId, UUID userId) {
        WithdrawalRequestEntity withdrawalRequestEntity = withdrawalService.getWithdrawalById(withdrawalRequestId);
        if (withdrawalRequestId == null) {
            log.error("Withdrawal request not found");
            throw new RuntimeException("Withdrawal request not found");
        }

        PaymentRequestEntity paymentRequestEntity = paymentRequestService.getPaymentRequestById(withdrawalRequestEntity.getPaymentRequestId());
        if (!paymentRequestEntity.getUserId().equals(userId)) {
            log.error("Withdrawal request user id mismatch");
            throw new RuntimeException("Withdrawal request user id mismatch");
        }

        if (!paymentRequestEntity.getStatus().equals(Status.ACTIVE)) {
            log.error("Payment request status is not ACTIVE");
            throw new RuntimeException("Payment request status is not ACTIVE");
        }

        WalletEntity wallet = walletService.getWalletEntityById(paymentRequestEntity.getWalletId());

        TransactionEntity transaction = new TransactionEntity().toBuilder()
                .uid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .userId(paymentRequestEntity.getUserId())
                .walletId(wallet.getUid())
                .paymentRequestId(withdrawalRequestEntity.getPaymentRequestId())
                .updatedAt(LocalDateTime.now())
                .amount(paymentRequestEntity.getAmount())
                .transactionType(TransactionType.WITHDRAWAL)
                .walletName(wallet.getName())
                .build();

        if (wallet.getBalance() < -paymentRequestEntity.getAmount()) {
            log.info("Insufficient balance for transaction {}", transaction);
            transaction.setState(TransactionState.FAILED);
        } else {
            log.info("Transaction {} has been successfully created", transaction);
            transaction.setState(TransactionState.CREATED);
        }

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        log.info("Withdrawal transaction has been saved: {}", savedTransaction);
        return savedTransaction;
    }

    @Transactional
    public TransactionEntity finalizeTransaction(UUID transactionId, TransactionState state, UUID userId) {
        TransactionEntity transactionInProgress = transactionRepository.findById(transactionId).orElse(null);
        if (transactionInProgress == null) {
            log.error("Transaction not found");
            throw new RuntimeException("Transaction not found");
        }

        String transactionType = transactionInProgress.getTransactionType().toString();
        if (!transactionInProgress.getState().equals(TransactionState.CREATED)) {
            log.error("Transaction status of type {} is different from CREATED", transactionType);
            throw new RuntimeException("Top up transaction status is different from CREATED");
        }

        if (!transactionInProgress.getUserId().equals(userId)) {
            log.error("{} request user id mismatch", transactionType);
            throw new RuntimeException(transactionType + " request user id mismatch");
        }

        transactionInProgress.setUpdatedAt(LocalDateTime.now());
        transactionInProgress.setState(TransactionState.IN_PROGRESS);

        TransactionEntity savedTransactionInProgress = transactionRepository.save(transactionInProgress);
        log.info("Transaction {} in progress {}", transactionType, savedTransactionInProgress);

        WalletEntity wallet = walletService.getWalletEntityById(transactionInProgress.getWalletId());
        walletService.updateWalletBalance(wallet, transactionInProgress.getAmount());
        PaymentRequestEntity paymentRequestEntity = paymentRequestService.getPaymentRequestById(transactionInProgress.getPaymentRequestId());
        paymentRequestService.updatePaymentRequestStatus(paymentRequestEntity, Status.PROCESSED);


        savedTransactionInProgress.setState(state);
        TransactionEntity savedFinalizedTransaction = transactionRepository.save(savedTransactionInProgress);
        log.info("Transaction {} finalized {}", transactionType, savedFinalizedTransaction);
        return savedFinalizedTransaction;
    }

    @Transactional
    public List<TransactionEntity> finalizeOneShardTransferTransaction(UUID transferRequestId) throws ExecutionException, InterruptedException {
        TransferRequestEntity transferRequest = transferService.getTransferById(transferRequestId);
        if (transferRequest == null) {
            log.error("Transfer request not found");
            throw new RuntimeException("Transfer request not found");
        }

        UUID paymentRequestIdFrom = transferRequest.getPaymentRequestIdFrom();
        UUID paymentRequestIdTo = transferRequest.getPaymentRequestIdTo();

        if (paymentRequestIdFrom == null || paymentRequestIdTo == null) {
            log.error("Payment request not found");
            throw new RuntimeException("Payment request not found");
        }

        PaymentRequestEntity paymentRequestEntityFrom = paymentRequestService.getPaymentRequestById(paymentRequestIdFrom);
        PaymentRequestEntity paymentRequestEntityTo = paymentRequestService.getPaymentRequestById(paymentRequestIdTo);

        double amount = paymentRequestEntityTo.getAmount();
        WalletEntity walletFrom = walletService.getWalletEntityById(paymentRequestEntityFrom.getWalletId());
        WalletEntity walletTo = walletService.getWalletEntityById(paymentRequestEntityTo.getWalletId());

        TransactionState state;

        if (walletFrom.getBalance() < amount) {
            state = TransactionState.FAILED;
        } else {
            state = TransactionState.SUCCESS;
        }

        TransactionEntity transactionFrom = new TransactionEntity().toBuilder()
                .uid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .userId(paymentRequestEntityFrom.getUserId())
                .walletId(walletFrom.getUid())
                .paymentRequestId(paymentRequestIdFrom)
                .updatedAt(LocalDateTime.now())
                .amount(paymentRequestEntityFrom.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .state(state)
                .walletName(walletFrom.getName())
                .build();

        TransactionEntity transactionTo = new TransactionEntity().toBuilder()
                .uid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .userId(paymentRequestEntityTo.getUserId())
                .walletId(walletTo.getUid())
                .paymentRequestId(paymentRequestIdTo)
                .updatedAt(LocalDateTime.now())
                .amount(paymentRequestEntityTo.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .state(state)
                .walletName(walletFrom.getName())
                .build();

        if (state.equals(TransactionState.SUCCESS)) {
            walletService.updateWalletBalance(walletFrom, paymentRequestEntityFrom.getAmount());
            paymentRequestService.updatePaymentRequestStatus(paymentRequestEntityFrom, Status.PROCESSED);
            log.info("Wallet from updated {}", walletFrom.getUid());

            walletService.updateWalletBalance(walletTo, paymentRequestEntityTo.getAmount());
            paymentRequestService.updatePaymentRequestStatus(paymentRequestEntityTo, Status.PROCESSED);
            log.info("Wallet to updated {}", walletTo.getUid());
        }

        TransactionEntity savedTransactionFrom = transactionRepository.save(transactionFrom);
        TransactionEntity savedTransactionTo = transactionRepository.save(transactionTo);

        log.info("Transfer transaction has been saved : from {}", savedTransactionFrom);
        log.info("Transfer transaction has been saved : to {}", savedTransactionTo);

        return List.of(savedTransactionFrom, savedTransactionTo);
    }

    @Transactional
    public List<TransactionEntity> finalizeCrossShardTransferTransaction(UUID transferRequestId, UUID userFromId, UUID userToId) throws ExecutionException, InterruptedException {
        TransferRequestEntity transferRequest = transferService.getTransferById(transferRequestId);
        if (transferRequest == null) {
            log.error("Transfer request not found");
            throw new RuntimeException("Transfer request not found");
        }

        UUID paymentRequestIdFrom = transferRequest.getPaymentRequestIdFrom();
        UUID paymentRequestIdTo = transferRequest.getPaymentRequestIdTo();

        if (paymentRequestIdFrom == null || paymentRequestIdTo == null) {
            log.error("Payment request not found");
            throw new RuntimeException("Payment request not found");
        }

        PaymentRequestEntity paymentRequestEntityFrom = paymentRequestService.getPaymentRequestById(paymentRequestIdFrom);
        PaymentRequestEntity paymentRequestEntityTo = paymentRequestService.getPaymentRequestById(paymentRequestIdTo);

        double amount = paymentRequestEntityTo.getAmount();
        WalletEntity walletFrom = walletService.getWalletEntityById(paymentRequestEntityFrom.getWalletId());
        WalletEntity walletTo = walletService.getWalletEntityById(paymentRequestEntityTo.getWalletId());

        TransactionState state;

        if (walletFrom.getBalance() < amount) {
            state = TransactionState.FAILED;
        } else {
            state = TransactionState.SUCCESS;
        }

        TransactionEntity transactionEntityFrom;
        TransactionEntity transactionEntityTo;

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<Object[]> shardFromFuture = executorService.submit(() -> {
                ShardUtils.setShard(userFromId);

                WalletEntity walletFromUpdated = walletFrom;
                WalletEntity walletToUpdated = walletTo;

                if (state.equals(TransactionState.SUCCESS)) {
                    walletFromUpdated = walletService.updateWalletBalance(walletFrom, paymentRequestEntityFrom.getAmount());
                    log.info("Wallet from updated in shard from {}", walletFrom.getUid());

                    walletToUpdated = walletService.updateWalletBalance(walletTo, paymentRequestEntityTo.getAmount());
                    log.info("Wallet to updated in shard from {}", walletTo.getUid());
                }

                TransactionEntity transactionFrom = transactionRepository.save(new TransactionEntity().toBuilder()
                        .uid(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .userId(paymentRequestEntityFrom.getUserId())
                        .walletId(walletFrom.getUid())
                        .paymentRequestId(paymentRequestIdFrom)
                        .updatedAt(LocalDateTime.now())
                        .amount(paymentRequestEntityFrom.getAmount())
                        .transactionType(TransactionType.TRANSFER)
                        .state(state)
                        .walletName(walletFrom.getName())
                        .build());

                TransactionEntity transactionTo = transactionRepository.save(new TransactionEntity().toBuilder()
                        .uid(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .userId(paymentRequestEntityTo.getUserId())
                        .walletId(walletTo.getUid())
                        .paymentRequestId(paymentRequestIdTo)
                        .updatedAt(LocalDateTime.now())
                        .amount(paymentRequestEntityTo.getAmount())
                        .transactionType(TransactionType.TRANSFER)
                        .state(state)
                        .walletName(walletTo.getName())
                        .build());


                PaymentRequestEntity paymentRequestFromUpdated = paymentRequestService.updatePaymentRequestStatus(paymentRequestEntityFrom, Status.PROCESSED);
                log.info("Payment request from updated in shard from {}", paymentRequestEntityFrom);

                PaymentRequestEntity paymentRequestToUpdated = paymentRequestService.updatePaymentRequestStatus(paymentRequestEntityTo, Status.PROCESSED);
                log.info("Payment request to updated in shard from {}", paymentRequestEntityTo);

                return new Object[]{transactionFrom, transactionTo, walletFromUpdated, walletToUpdated, paymentRequestFromUpdated, paymentRequestToUpdated};

            });


            Object[] savedEntitiesShardFrom = shardFromFuture.get();

            TransactionEntity transactionFrom = (TransactionEntity) savedEntitiesShardFrom[0];
            TransactionEntity transactionTo = (TransactionEntity) savedEntitiesShardFrom[1];
            WalletEntity walletFromSaved = (WalletEntity) savedEntitiesShardFrom[2];
            WalletEntity walletToSaved = (WalletEntity) savedEntitiesShardFrom[3];
            PaymentRequestEntity paymentRequestFromSaved = (PaymentRequestEntity) savedEntitiesShardFrom[4];
            PaymentRequestEntity paymentRequestToSaved = (PaymentRequestEntity) savedEntitiesShardFrom[5];


            Future<Object[]> shardToFuture = executorService.submit(() -> {
                        ShardUtils.setShard(userToId);
                        TransactionEntity transactionFromSaved = transactionRepository.save(transactionFrom);
                        TransactionEntity transactionToSaved = transactionRepository.save(transactionTo);

                        WalletEntity walletFromShardToSaved = walletService.addNewWallet(walletFromSaved);
                        WalletEntity walletToShardToSaved = walletService.addNewWallet(walletToSaved);

                        PaymentRequestEntity paymentRequestEntityFromSaved = paymentRequestService.addPaymentRequest(paymentRequestFromSaved);
                        PaymentRequestEntity paymentRequestEntityToSaved = paymentRequestService.addPaymentRequest(paymentRequestToSaved);

                        return new Object[]{transactionFromSaved, transactionToSaved, walletFromShardToSaved, walletToShardToSaved, paymentRequestEntityFromSaved, paymentRequestEntityToSaved};
                    }
            );

            Object[] savedEntitiesShardTo = shardToFuture.get();

            transactionEntityFrom = (TransactionEntity) savedEntitiesShardTo[0];
            transactionEntityTo = (TransactionEntity) savedEntitiesShardTo[1];

            log.info("Transfer transaction has been saved : from {}", transactionEntityFrom);
            log.info("Transfer transaction has been saved : to {}", transactionEntityTo);

            return List.of(transactionEntityFrom, transactionEntityTo);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }


}
