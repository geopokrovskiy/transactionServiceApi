package com.geopokrovskiy.kafka;

import com.geopokrovskiy.service.TransactionKafkaEventProcessor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class KafkaConsumer {

    private final TransactionKafkaEventProcessor transactionKafkaEventProcessor;

    @KafkaListener(topics = "#{'${kafka.topics}'.split(',')}")
    public void consume(GenericRecord record) {
        log.info("Record received: {} ", record);
        try {
            transactionKafkaEventProcessor.processRecord(record);
            log.info("Record {} has been successfully processed", record);
        } catch (Exception e) {
            log.error("Record {} has not been processed ", record);
            log.error(e.getMessage());
        }
    }
}
