package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpResponseDto;
import com.geopokrovskiy.dto.transaction_service.transfer.TransferCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.transfer.TransferResponseDto;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.mapper.payment_request.TransferMapper;
import com.geopokrovskiy.service.TransferService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@AllArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final TransferMapper transferMapper;

    @PostMapping
    public ResponseEntity<TransferResponseDto> createTopUp(@RequestBody TransferCreateRequestDto transferCreateRequestDto) {
        try {
            TransferRequestEntity transferRequestEntity = transferMapper.map(transferCreateRequestDto);

            // TODO manage the situation when the request entities are not in the shard by default
            // and provoke a Null Pointer Exception
            //
            UUID[] usersFromAndToIds = transferService.getToAndFromUsersIdFromTransferRequest(transferRequestEntity);
            UUID userFromId = usersFromAndToIds[0];
            UUID userToId = usersFromAndToIds[1];
            if (ShardUtils.determineShard(userFromId).equals(ShardUtils.determineShard(userToId))) {
                try {
                    this.setShard(userFromId);
                    TransferRequestEntity savedTransferRequestEntity = transferService.addNewTransfer(transferRequestEntity, userFromId);
                    TransferResponseDto transferResponseDto = transferMapper.map(savedTransferRequestEntity);
                    return new ResponseEntity<>(transferResponseDto, HttpStatusCode.valueOf(201));
                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatusCode.valueOf(400));
                }
            } else {
                this.setShard(userFromId);
                TransferRequestEntity savedTransferRequestEntity = transferService.addNewTransfer(transferRequestEntity, userFromId);

                this.setShard(userToId);
                savedTransferRequestEntity = transferService.addNewTransfer(transferRequestEntity, userFromId);
                TransferResponseDto transferResponseDto = transferMapper.map(savedTransferRequestEntity);
                return new ResponseEntity<>(transferResponseDto, HttpStatusCode.valueOf(201));
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    private void setShard(UUID userId) {
        String shard = ShardUtils.determineShard(userId);
        ShardContextHolder.setCurrentShard(shard);
    }

}
