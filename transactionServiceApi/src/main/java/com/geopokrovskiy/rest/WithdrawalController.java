package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.transaction.TransactionFinalizeDto;
import com.geopokrovskiy.dto.transaction_service.transaction.TransactionResponseDto;
import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.withdrawal.WithdrawalResponseDto;
import com.geopokrovskiy.entity.payment_request.WithdrawalRequestEntity;
import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.mapper.payment_request.WithdrawalMapper;
import com.geopokrovskiy.mapper.transaction.TransactionMapper;
import com.geopokrovskiy.service.TransactionService;
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

    private final TransactionService transactionService;

    private final WithdrawalMapper withdrawalMapper;

    private final TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<WithdrawalResponseDto> createWithdrawal(@RequestBody WithdrawalCreateRequestDto withdrawalCreateRequestDto, @RequestHeader("Cookie") UUID userId) {
        try {
            ShardUtils.setShard(userId);
            WithdrawalRequestEntity withdrawalRequestEntityToSave = withdrawalMapper.map(withdrawalCreateRequestDto);
            WithdrawalRequestEntity savedWithdrawalRequestEntity = withdrawalService.addNewWithdrawal(withdrawalRequestEntityToSave, userId);
            WithdrawalResponseDto withdrawalResponseDto = withdrawalMapper.map(savedWithdrawalRequestEntity);
            return new ResponseEntity<>(withdrawalResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/transaction/process/{withdrawalRequestId}")
    public ResponseEntity<TransactionResponseDto> processWithdrawalTransaction(@PathVariable UUID withdrawalRequestId, @RequestHeader("Cookie") UUID userId) {
        try {
            ShardUtils.setShard(userId);
            TransactionEntity withdrawalRequestCreated = transactionService.createNewWithdrawalTransaction(withdrawalRequestId, userId);
            TransactionResponseDto transactionResponseDto = transactionMapper.map(withdrawalRequestCreated);
            return new ResponseEntity<>(transactionResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PatchMapping("/transaction/finalize/{withdrawalTransactionId}")
    public ResponseEntity<TransactionResponseDto> processTransaction(@PathVariable UUID withdrawalTransactionId,
                                                                     @RequestBody TransactionFinalizeDto transactionFinalizeDto,
                                                                     @RequestHeader("Cookie") UUID userId) {
        try {
            TransactionState transactionState = TransactionState.valueOf(transactionFinalizeDto.getTransactionState());
            ShardUtils.setShard(userId);
            TransactionEntity withdrawalRequestInProgress = transactionService.finalizeTransaction(withdrawalTransactionId, transactionState, userId);
            TransactionResponseDto transactionResponseDto = transactionMapper.map(withdrawalRequestInProgress);
            return new ResponseEntity<>(transactionResponseDto, HttpStatusCode.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

}
