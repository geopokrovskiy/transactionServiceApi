package com.geopokrovskiy.rest;

import com.geopokrovskiy.dto.transaction_service.transfer.TransferCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.transfer.TransferResponseDto;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.mapper.payment_request.TransferMapper;
import com.geopokrovskiy.service.TransferService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/v1/transfers")
@AllArgsConstructor
@Slf4j
public class TransferController {
    private final TransferService transferService;
    private final TransferMapper transferMapper;

    @PostMapping
    public ResponseEntity<TransferResponseDto> createTopUp(@RequestBody TransferCreateRequestDto transferCreateRequestDto) {
        try {
            TransferRequestEntity transferRequestEntity = transferMapper.map(transferCreateRequestDto);

            UUID userFromId = transferRequestEntity.getUserFromId();
            UUID userToId = transferRequestEntity.getUserToId();

            String shardFrom = ShardUtils.determineShard(userFromId);
            String shardTo = ShardUtils.determineShard(userToId);

            if (shardFrom.equals(shardTo)) {
                try {
                    ShardUtils.setShard(userFromId);
                    if (!transferService.isWalletValid(transferRequestEntity.getWalletFromId(), userFromId)) {
                        log.error("Incompatible sender wallet input");
                        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
                    }

                    if (!transferService.isWalletValid(transferRequestEntity.getWalletToId(), userToId)) {
                        log.error("Incompatible beneficiary wallet input");
                        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
                    }

                    TransferRequestEntity savedTransferRequestEntity = transferService.addNewTransfer(transferRequestEntity);
                    TransferResponseDto transferResponseDto = transferMapper.map(savedTransferRequestEntity);

                    return new ResponseEntity<>(transferResponseDto, HttpStatusCode.valueOf(201));

                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatusCode.valueOf(400));
                }
            } else {
                ArrayList<Future<Boolean>> checkWalletsFutures = new ArrayList<>(2);
                try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {

                    // verification of sender
                    Future<Boolean> checkWalletFromFuture = executorService.submit(() -> {
                        ShardUtils.setShard(userFromId);
                        return transferService.isWalletValid(transferRequestEntity.getWalletFromId(), userFromId);
                    });
                    checkWalletsFutures.add(checkWalletFromFuture);

                    // verification of recipient
                    ShardUtils.setShard(userFromId);
                    Future<Boolean> checkWalletToFuture = executorService.submit(() -> {
                        ShardUtils.setShard(userToId);
                        return transferService.isWalletValid(transferRequestEntity.getWalletToId(), userToId);
                    });
                    checkWalletsFutures.add(checkWalletToFuture);

                    if (!checkWalletsFutures.get(0).get()) {
                        log.error("Incompatible sender wallet input");
                        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
                    }

                    if (!checkWalletsFutures.get(1).get()) {
                        log.error("Incompatible beneficiary wallet input");
                        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
                    }

                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatusCode.valueOf(500));
                }
                TransferRequestEntity savedTransferRequestEntity = transferService.addNewTransfer(transferRequestEntity);
                TransferResponseDto transferResponseDto = transferMapper.map(savedTransferRequestEntity);
                return new ResponseEntity<>(transferResponseDto, HttpStatusCode.valueOf(201));
            }
        } catch (
                Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

}
