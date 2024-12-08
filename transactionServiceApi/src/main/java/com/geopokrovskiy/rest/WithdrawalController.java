package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalResponseDto;
import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import com.geopokrovskiy.mapper.payment_request.WithdrawalMapper;
import com.geopokrovskiy.service.WithdrawalService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/withdrawals/user")
@AllArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    private final WithdrawalMapper withdrawalMapper;

    @PostMapping
    public ResponseEntity<WithdrawalResponseDto> createTopUp(@RequestBody WithdrawalCreateRequestDto withdrawalCreateRequestDto, @RequestHeader("Cookie") UUID userId) {
        try {
            this.setShard(userId);
            WithdrawalRequestEntity withdrawalRequestEntityToSave = withdrawalMapper.map(withdrawalCreateRequestDto);
            WithdrawalRequestEntity savedWithdrawalRequestEntity = withdrawalService.addNewWithdrawal(withdrawalRequestEntityToSave, userId);
            WithdrawalResponseDto withdrawalResponseDto = withdrawalMapper.map(savedWithdrawalRequestEntity);
            return new ResponseEntity<>(withdrawalResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    private void setShard(UUID userId) {
        String shard = ShardUtils.determineShard(userId);
        ShardContextHolder.setCurrentShard(shard);
    }
}
