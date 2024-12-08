package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.top_up.TopUpResponseDto;
import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import com.geopokrovskiy.mapper.payment_request.TopUpMapper;
import com.geopokrovskiy.service.TopUpService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.AllArgsConstructor;
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

    @PostMapping
    public ResponseEntity<TopUpResponseDto> createTopUp(@RequestBody TopUpCreateRequestDto topUpCreateRequestDto, @RequestHeader("Cookie") UUID userId) {
        try {
            this.setShard(userId);
            TopUpRequestEntity topUpRequestEntityToSave = topUpMapper.map(topUpCreateRequestDto);
            TopUpRequestEntity savedTopUpRequestEntity = topUpService.addNewTopUp(topUpRequestEntityToSave, userId);
            TopUpResponseDto topUpResponseDto = topUpMapper.map(savedTopUpRequestEntity);
            return new ResponseEntity<>(topUpResponseDto, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    private void setShard(UUID userId) {
        String shard = ShardUtils.determineShard(userId);
        ShardContextHolder.setCurrentShard(shard);
    }


}
