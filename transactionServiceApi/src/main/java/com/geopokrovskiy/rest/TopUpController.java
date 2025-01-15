package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpResponseDto;
import com.geopokrovskiy.dto.transaction_service.transaction.TransactionFinalizeDto;
import com.geopokrovskiy.dto.transaction_service.transaction.TransactionResponseDto;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.entity.transaction.TransactionEntity;
import com.geopokrovskiy.entity.transaction.TransactionState;
import com.geopokrovskiy.mapper.payment_request.TopUpMapper;
import com.geopokrovskiy.mapper.transaction.TransactionMapper;
import com.geopokrovskiy.service.TopUpService;
import com.geopokrovskiy.service.TransactionService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/top_ups/user")
@AllArgsConstructor
public class TopUpController {

    private final TopUpService topUpService;

    private final TopUpMapper topUpMapper;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<TopUpResponseDto> createTopUp(@RequestBody TopUpCreateRequestDto topUpCreateRequestDto, @RequestHeader("Cookie") UUID userId) {
        try {
            ShardUtils.setShard(userId);
            TopUpRequestEntity topUpRequestEntityToSave = topUpMapper.map(topUpCreateRequestDto);
            TopUpRequestEntity savedTopUpRequestEntity = topUpService.addNewTopUp(topUpRequestEntityToSave, userId);
            TopUpResponseDto topUpResponseDto = topUpMapper.map(savedTopUpRequestEntity);
            return new ResponseEntity<>(topUpResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/transaction/process/{topUpRequestId}")
    public ResponseEntity<TransactionResponseDto> processTopUpTransaction(@PathVariable UUID topUpRequestId, @RequestHeader("Cookie") UUID userId) {
        try {
            ShardUtils.setShard(userId);
            TransactionEntity topUpRequestCreated = transactionService.createNewTopUpTransaction(topUpRequestId, userId);
            TransactionResponseDto transactionResponseDto = transactionMapper.map(topUpRequestCreated);
            return new ResponseEntity<>(transactionResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PatchMapping("/transaction/finalize/{topUpTransactionId}")
    public ResponseEntity<TransactionResponseDto> processTransaction(@PathVariable UUID topUpTransactionId,
                                                                     @RequestBody TransactionFinalizeDto transactionFinalizeDto,
                                                                     @RequestHeader("Cookie") UUID userId) {
        try {
            TransactionState transactionState = TransactionState.valueOf(transactionFinalizeDto.getTransactionState());
            ShardUtils.setShard(userId);
            TransactionEntity topUpRequestInProgress = transactionService.finalizeTransaction(topUpTransactionId, transactionState, userId);
            TransactionResponseDto transactionResponseDto = transactionMapper.map(topUpRequestInProgress);
            return new ResponseEntity<>(transactionResponseDto, HttpStatusCode.valueOf(200));
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }





}
