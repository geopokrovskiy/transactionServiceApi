package com.geopokrovskiy.mapper.wallet;

import com.geopokrovskiy.dto.transaction_service.wallet.WalletCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.wallet.WalletResponseDto;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @InheritInverseConfiguration
    WalletResponseDto map(WalletEntity wallet);

}
