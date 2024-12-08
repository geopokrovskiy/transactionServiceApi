package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.repository.PaymentRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class PaymentRequestService {
    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentRequestEntity addNewPaymentRequest(PaymentRequestEntity paymentRequestEntity) {
        return paymentRequestRepository.save(paymentRequestEntity);
    }

    public PaymentRequestEntity getPaymentRequestById(UUID id) {
        return paymentRequestRepository.findById(id).orElse(null);
    }
}
