package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.repository.PaymentRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class PaymentRequestService {
    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentRequestEntity addNewPaymentRequest(PaymentRequestEntity paymentRequestEntity) {
        paymentRequestEntity.setUid(UUID.randomUUID());
        return paymentRequestRepository.save(paymentRequestEntity);
    }

    public PaymentRequestEntity getPaymentRequestById(UUID id) {
        return paymentRequestRepository.findById(id).orElse(null);
    }

    public PaymentRequestEntity addPaymentRequest(PaymentRequestEntity paymentRequestEntity) {
        paymentRequestEntity.setUid(paymentRequestEntity.getUid());
        return paymentRequestRepository.save(paymentRequestEntity);
    }

    public PaymentRequestEntity updatePaymentRequestStatus(PaymentRequestEntity paymentRequestEntity, Status status) {
        paymentRequestEntity.setStatus(status);
        paymentRequestEntity.setModifiedAt(LocalDateTime.now());
        return paymentRequestRepository.save(paymentRequestEntity);
    }

}
