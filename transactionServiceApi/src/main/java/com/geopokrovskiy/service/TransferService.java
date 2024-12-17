package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.repository.TransferRepository;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@AllArgsConstructor
@Service
@Slf4j
public class TransferService {

    private final TransferRepository transferRepository;
    private final PaymentRequestService paymentRequestService;
    private final WalletService walletService;

    @Transactional
    public TransferRequestEntity addNewTransfer(TransferRequestEntity transferRequestEntity) {

        UUID userFromId = transferRequestEntity.getUserFromId();
        UUID userToId = transferRequestEntity.getUserToId();
        String shardFrom = ShardUtils.determineShard(userFromId);
        String shardTo = ShardUtils.determineShard(userToId);

        PaymentRequestEntity paymentRequestEntityFrom = new PaymentRequestEntity().
                toBuilder().
                createdAt(LocalDateTime.now()).
                modifiedAt(LocalDateTime.now()).
                userId(transferRequestEntity.getUserFromId()).
                amount((-1) * transferRequestEntity.getAmount()).
                status(Status.ACTIVE).
                walletId(transferRequestEntity.getWalletFromId()).
                comment(transferRequestEntity.getComment()).
                paymentMethodId(transferRequestEntity.getPaymentMethodId()).
                build();

        PaymentRequestEntity paymentRequestEntityTo = new PaymentRequestEntity().
                toBuilder().
                createdAt(LocalDateTime.now()).
                modifiedAt(LocalDateTime.now()).
                userId(transferRequestEntity.getUserToId()).
                walletId(transferRequestEntity.getWalletToId()).
                amount(transferRequestEntity.getAmount()).
                status(Status.ACTIVE).
                comment(transferRequestEntity.getComment()).
                paymentMethodId(transferRequestEntity.getPaymentMethodId()).
                build();


        if (!shardFrom.equals(shardTo)) {
            WalletEntity walletEntityFrom = null;
            WalletEntity walletEntityTo = null;
            try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
                Future<WalletEntity> walletEntityFromFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userFromId);
                    return walletService.getWalletEntityById(transferRequestEntity.getWalletFromId());
                });

                Future<WalletEntity> walletEntityToFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userToId);
                    return walletService.getWalletEntityById(transferRequestEntity.getWalletToId());
                });

                walletEntityFrom = walletEntityFromFuture.get();
                walletEntityTo = walletEntityToFuture.get();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            WalletEntity walletEntityFromFinal = walletEntityFrom;
            WalletEntity walletEntityToFinal = walletEntityTo;
            try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
                Future<WalletEntity> savedWalletEntityFromFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userFromId);
                    return walletService.addNewWallet(walletEntityToFinal);
                });

                Future<WalletEntity> savedWalletEntityToFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userToId);
                    return walletService.addNewWallet(walletEntityFromFinal);
                });

                WalletEntity savedWalletEntityFrom = savedWalletEntityFromFuture.get();
                WalletEntity savedWalletEntityTo = savedWalletEntityToFuture.get();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {

                Future<Object[]> fromShardEntitiesFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userFromId);
                    PaymentRequestEntity savedPaymentEntityFrom = paymentRequestService.addNewPaymentRequest(paymentRequestEntityFrom);
                    PaymentRequestEntity savedPaymentEntityTo = paymentRequestService.addNewPaymentRequest(paymentRequestEntityTo);

                    TransferRequestEntity transferRequestToSave = new TransferRequestEntity().
                            toBuilder().
                            createdAt(LocalDateTime.now()).
                            modifiedAt(LocalDateTime.now()).
                            walletToId(transferRequestEntity.getWalletToId()).
                            walletFromId(transferRequestEntity.getWalletFromId()).
                            userToId(transferRequestEntity.getUserToId()).
                            userFromId(transferRequestEntity.getUserFromId()).
                            amount(transferRequestEntity.getAmount()).
                            status(Status.ACTIVE).
                            comment(transferRequestEntity.getComment()).
                            paymentMethodId(transferRequestEntity.getPaymentMethodId()).
                            paymentRequestIdTo(savedPaymentEntityTo.getUid()).
                            paymentRequestIdFrom(savedPaymentEntityFrom.getUid()).
                            systemRate("1.0").
                            uid(UUID.randomUUID()).
                            build();

                    TransferRequestEntity savedTransferRequestEntity = transferRepository.save(transferRequestToSave);

                    return new Object[]{savedPaymentEntityFrom, savedPaymentEntityTo, savedTransferRequestEntity};
                });

                Object[] savedEntitiesShardFrom = fromShardEntitiesFuture.get();

                PaymentRequestEntity savedPaymentEntityFromShardFrom = (PaymentRequestEntity) savedEntitiesShardFrom[0];
                PaymentRequestEntity savedPaymentEntityToShardFrom = (PaymentRequestEntity) savedEntitiesShardFrom[1];
                TransferRequestEntity savedTransferRequestEntityShardFrom = (TransferRequestEntity) savedEntitiesShardFrom[2];

                Future<Object[]> toShardEntitiesFuture = executorService.submit(() -> {
                    ShardUtils.setShard(userToId);
                    PaymentRequestEntity savedPaymentEntityFromShardTo = paymentRequestService.addPaymentRequest(savedPaymentEntityFromShardFrom);
                    PaymentRequestEntity savedPaymentEntityToShardTo = paymentRequestService.addPaymentRequest(savedPaymentEntityToShardFrom);
                    TransferRequestEntity savedTransferEntityShardTo = transferRepository.save(savedTransferRequestEntityShardFrom);
                    return new Object[]{savedPaymentEntityFromShardTo, savedPaymentEntityToShardTo, savedTransferEntityShardTo};
                });

                Object[] savedEntitiesShardTo = toShardEntitiesFuture.get();

                PaymentRequestEntity savedPaymentEntityFromShardTo = (PaymentRequestEntity) savedEntitiesShardTo[0];
                PaymentRequestEntity savedPaymentEntityToShardTo = (PaymentRequestEntity) savedEntitiesShardTo[1];
                TransferRequestEntity savedTransferRequestEntityShardTo = (TransferRequestEntity) savedEntitiesShardTo[2];

                transferRequestEntity.setUid(savedTransferRequestEntityShardTo.getUid());
                transferRequestEntity.setCreatedAt(savedTransferRequestEntityShardFrom.getCreatedAt());
                transferRequestEntity.setPaymentRequestIdFrom(savedPaymentEntityFromShardTo.getUid());
                transferRequestEntity.setPaymentRequestIdTo(savedPaymentEntityToShardTo.getUid());
                transferRequestEntity.setStatus(Status.ACTIVE);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return transferRequestEntity;
        } else {

            paymentRequestService.addNewPaymentRequest(paymentRequestEntityFrom);
            paymentRequestService.addNewPaymentRequest(paymentRequestEntityTo);


            TransferRequestEntity transferRequest = new TransferRequestEntity().
                    toBuilder().
                    createdAt(LocalDateTime.now()).
                    modifiedAt(LocalDateTime.now()).
                    walletToId(transferRequestEntity.getWalletToId()).
                    walletFromId(transferRequestEntity.getWalletFromId()).
                    userToId(transferRequestEntity.getUserToId()).
                    userFromId(transferRequestEntity.getUserFromId()).
                    amount(transferRequestEntity.getAmount()).
                    status(Status.ACTIVE).
                    comment(transferRequestEntity.getComment()).
                    paymentMethodId(transferRequestEntity.getPaymentMethodId()).
                    paymentRequestIdTo(paymentRequestEntityTo.getUid()).
                    paymentRequestIdFrom(paymentRequestEntityFrom.getUid()).
                    systemRate("1.0").
                    uid(UUID.randomUUID()).
                    build();

            TransferRequestEntity savedTransferRequestEntity = transferRepository.save(transferRequest);
            transferRequest.setUid(savedTransferRequestEntity.getUid());
            return transferRequest;
        }
    }

    public boolean isWalletValid(UUID walletId, UUID userId) {
        log.info(ShardContextHolder.getCurrentShard());
        WalletEntity walletEntity = walletService.getWalletEntityById(walletId);
        if (walletEntity == null) {

            log.error("Wallet not found");
            return false;
        }

        if (!walletEntity.getUserId().equals(userId)) {
            log.error("Wallet doesn't match");
            return false;
        }
        return true;
    }


}
