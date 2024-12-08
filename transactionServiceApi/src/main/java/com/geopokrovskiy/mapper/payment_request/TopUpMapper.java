package com.geopokrovskiy.mapper.payment_request;

import com.geopokrovskiy.dto.transaction_service.top_up.TopUpCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpResponseDto;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopUpMapper {

    TopUpResponseDto map(TopUpRequestEntity topUpRequestEntity);

    TopUpRequestEntity map(TopUpCreateRequestDto topUpCreateRequestDto);
}
