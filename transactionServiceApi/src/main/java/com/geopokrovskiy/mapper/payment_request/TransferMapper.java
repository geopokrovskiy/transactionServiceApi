package com.geopokrovskiy.mapper.payment_request;

import com.geopokrovskiy.dto.transaction_service.transfer.TransferCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.transfer.TransferResponseDto;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    TransferResponseDto map(TransferRequestEntity transferRequestEntity);

    TransferRequestEntity map(TransferCreateRequestDto transferCreateRequestDto);
}
