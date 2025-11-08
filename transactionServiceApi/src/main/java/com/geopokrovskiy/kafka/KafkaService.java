package com.geopokrovskiy.kafka;

import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.service.TransactionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Data
@Component
public class KafkaService {

    private final TransactionService transactionService;

    @KafkaListener(topics = "#{'${kafka.topics}'.split(',')}")
    public void consume(GenericRecord record) {

        // Receiving a message from Kafka
        log.info("Record received: {} ", record);
        String externalTransactionId = record.get("provider_transaction_uid").toString();
        TransactionState transactionState = TransactionState.valueOf(record.get("transaction_status").toString());

        TransactionEntity transaction = transactionService.getTransactionByExternalProviderId(UUID.fromString(externalTransactionId));
        UUID transactionId = transaction.getUid();

        try {
            transactionService.finalizeTransaction(transactionId, transactionState, transaction.getUserId());
            log.info("Transaction {} has been finalized with status {}", transactionId, transactionState);
        } catch (Exception e) {
            log.error("Failed to finalize transaction {}, {}", transactionId, e.getMessage());
        }

    }
}
