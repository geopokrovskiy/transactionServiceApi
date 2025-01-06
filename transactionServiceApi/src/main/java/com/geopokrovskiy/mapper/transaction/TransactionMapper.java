package com.geopokrovskiy.mapper.transaction;

import com.geopokrovskiy.dto.transaction_service.transaction.TransactionResponseDto;
import com.geopokrovskiy.entity.transaction.TransactionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @InheritInverseConfiguration
    TransactionResponseDto map(TransactionEntity transactionEntity);
}
