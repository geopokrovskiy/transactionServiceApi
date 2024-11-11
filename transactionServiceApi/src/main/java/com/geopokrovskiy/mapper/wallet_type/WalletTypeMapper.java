package com.geopokrovskiy.mapper.wallet_type;

import com.geopokrovskiy.dto.transaction_service.wallet_type.WalletTypeResponseDto;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletTypeMapper {

    @InheritInverseConfiguration
    WalletTypeResponseDto map(WalletTypeEntity walletType);
}
