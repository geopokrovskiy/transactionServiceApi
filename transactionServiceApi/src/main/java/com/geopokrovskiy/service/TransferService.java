package com.geopokrovskiy.service;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.repository.TransferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class TransferService {

    private final TransferRepository transferRepository;
    private final PaymentRequestService paymentRequestService;
    private final WalletService walletService;

    @Transactional
    public TransferRequestEntity addNewTransfer(TransferRequestEntity transferRequestEntity, UUID userFromId) {
        UUID paymentRequestEntityFromId = transferRequestEntity.getPaymentRequestIdFrom();
        PaymentRequestEntity paymentRequestEntityFrom = paymentRequestService.getPaymentRequestById(paymentRequestEntityFromId);
        if (paymentRequestEntityFrom == null) {
            log.error("Payment request from with such id does not exist");
            throw new IllegalArgumentException("Payment request from with such id does not exist");
        }

        UUID paymentRequestEntityToId = transferRequestEntity.getPaymentRequestIdTo();
        PaymentRequestEntity paymentRequestTo = paymentRequestService.getPaymentRequestById(paymentRequestEntityToId);
        if (paymentRequestTo == null) {
            log.error("Payment request to with such id does not exist");
            throw new IllegalArgumentException("Payment request to with such id does not exist");
        }
        try {
            PaymentRequestEntity paymentRequestToSave = new PaymentRequestEntity().toBuilder()
                    .amount(transferRequestEntity.getAmount())
                    .comment(transferRequestEntity.getComment())
                    .paymentMethodId(transferRequestEntity.getPaymentMethodId())
                    .userId(userFromId)
                    .createdAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now())
                    .walletId(paymentRequestEntityFrom.getWalletId())
                    .status(Status.ACTIVE)
                    .build();
            PaymentRequestEntity savedPaymentRequest = paymentRequestService.addNewPaymentRequest(paymentRequestToSave);
            log.info("Saved PaymentRequestEntity Transfer: {}", savedPaymentRequest);

            TransferRequestEntity transferRequestEntityToSave = new TransferRequestEntity().toBuilder()
                    .createdAt(savedPaymentRequest.getCreatedAt())
                    .modifiedAt(savedPaymentRequest.getModifiedAt())
                    .comment(savedPaymentRequest.getComment())
                    .paymentRequestIdFrom(savedPaymentRequest.getUid())
                    .paymentRequestIdTo(transferRequestEntity.getPaymentRequestIdTo())
                    .paymentMethodId(savedPaymentRequest.getPaymentMethodId())
                    .amount(savedPaymentRequest.getAmount())
                    .walletId(savedPaymentRequest.getWalletId())
                    .status(Status.ACTIVE)
                    .userId(userFromId)
                    .build();
            TransferRequestEntity savedTransfer = transferRepository.save(transferRequestEntityToSave);
            ShardContextHolder.clearShard();
            return savedTransfer;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public UUID[] getToAndFromUsersIdFromTransferRequest(TransferRequestEntity transferRequestEntity) {
        UUID paymentRequestEntityFromId = transferRequestEntity.getPaymentRequestIdFrom();
        PaymentRequestEntity paymentRequestEntityFrom = paymentRequestService.getPaymentRequestById(paymentRequestEntityFromId);
        UUID userFromId = paymentRequestEntityFrom.getUserId();

        UUID paymentRequestEntityToId = transferRequestEntity.getPaymentRequestIdTo();
        PaymentRequestEntity paymentRequestEntityTo = paymentRequestService.getPaymentRequestById(paymentRequestEntityToId);
        UUID userToId = paymentRequestEntityTo.getUserId();

        return new UUID[]{userFromId, userToId};
    }
}
