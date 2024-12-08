package com.geopokrovskiy.mapper.payment_request;

import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalResponseDto;
import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {
    WithdrawalResponseDto map(WithdrawalRequestEntity withdrawalRequestEntity);

    WithdrawalRequestEntity map(WithdrawalCreateRequestDto withdrawalCreateRequestDto);
}
