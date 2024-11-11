package com.geopokrovskiy.rest;

import com.geopokrovskiy.dto.transaction_service.wallet_type.WalletTypeResponseDto;
import com.geopokrovskiy.entity.wallet_type.WalletTypeEntity;
import com.geopokrovskiy.mapper.wallet_type.WalletTypeMapper;
import com.geopokrovskiy.service.WalletTypeService;
import lombok.Data;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Data
@RequestMapping("/api/v1/wallet_types")
public class WalletTypeController {

    private final WalletTypeService walletTypeService;
    private final WalletTypeMapper walletTypeMapper;

    @GetMapping("/list")
    public ResponseEntity<List<WalletTypeResponseDto>> getAllWalletTypes() {
        try {
            List<WalletTypeEntity> walletTypeEntities = walletTypeService.getAllWalletTypes();
            return new ResponseEntity<>(walletTypeEntities.stream().map(walletTypeMapper::map).toList(),
                    HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(500));
        }
    }
}
