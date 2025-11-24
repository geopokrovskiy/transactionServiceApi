package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.exception.TransactionNotFoundException;
import com.geopokrovskiy.service.utils.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@Data
public class TransactionKafkaEventProcessor {
    private final TransactionService transactionService;

    public void processRecord(GenericRecord record) {
        String externalTransactionId = record.get(Constants.PROVIDER_TRANSACTION_UUID).toString();
        TransactionState transactionState = TransactionState.valueOf(record.get(Constants.TRANSACTION_STATUS).toString());

        TransactionEntity transaction = transactionService.getTransactionByExternalProviderId(UUID.fromString(externalTransactionId));
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction entity with external id " + externalTransactionId +
                    " has not been found", ErrorCodes.TRANSACTION_NOT_FOUND);
        }
        UUID transactionId = transaction.getUid();

        try {
            transactionService.finalizeTransaction(transactionId, transactionState, transaction.getUserId());
            log.info("Transaction {} has been finalized with status {}", transactionId, transactionState);
        } catch (Exception e) {
            log.error("Failed to finalize transaction {}, {}", transactionId, e.getMessage());
            throw e;
        }

    }
}
