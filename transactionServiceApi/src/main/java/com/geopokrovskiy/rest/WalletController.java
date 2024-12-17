package com.geopokrovskiy.rest;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;
import com.geopokrovskiy.dto.transaction_service.wallet.WalletCreateRequestDto;
import com.geopokrovskiy.dto.transaction_service.wallet.WalletResponseDto;
import com.geopokrovskiy.entity.wallet.WalletEntity;
import com.geopokrovskiy.mapper.wallet.WalletMapper;
import com.geopokrovskiy.service.WalletService;
import com.geopokrovskiy.utils.ShardUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Data
@RequestMapping("/api/v1/wallets/user")
@Slf4j
public class WalletController {

    private final WalletService walletService;

    private final WalletMapper walletMapper;

    @PostMapping
    public ResponseEntity<WalletResponseDto> createNewWallet(@RequestBody WalletCreateRequestDto walletCreateRequestDto, @RequestHeader("Cookie") UUID userId) {
        try {
            ShardUtils.setShard(userId);
            WalletEntity savedEntity = walletService.addNewWallet(new WalletEntity().toBuilder()
                    .name(walletCreateRequestDto.getName())
                    .userId(userId)
                    .build(), walletCreateRequestDto.getCurrencyCode());
            WalletResponseDto walletResponseDto = walletMapper.map(savedEntity);
            log.info("A new wallet has been successfully created!");
            return new ResponseEntity<>(walletResponseDto, HttpStatusCode.valueOf(201));
        } catch (IllegalArgumentException e) {
            log.warn("A wallet couldn't be created because of a non existent wallet type");
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        } catch (DataIntegrityViolationException e) {
            log.warn("A wallet couldn't be created because of an incorrect request");
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        } catch (Exception e) {
            log.warn("A wallet couldn't be created because of a server error");
            return new ResponseEntity<>(HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WalletResponseDto>> getAllWalletsOfUser(@PathVariable UUID userId) {
        ShardUtils.setShard(userId);
        List<WalletEntity> retrievedWallets = walletService.getAllWalletsOfAUser(userId);
        return new ResponseEntity<>(retrievedWallets.stream().map(walletMapper::map).toList(),
                HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{userId}/{currencyCode}")
    public ResponseEntity<List<WalletResponseDto>> getAllWalletsOfUserWithCurrency(@PathVariable UUID userId, @PathVariable String currencyCode) {
        try {
            ShardUtils.setShard(userId);
            List<WalletEntity> retrievedWallets = walletService.getAllWalletsOfAUserWithCurrency(userId, currencyCode);
            return new ResponseEntity<>(retrievedWallets.stream().map(walletMapper::map).toList(), HttpStatusCode.valueOf(200));
        } catch (IllegalArgumentException e) {
            log.warn("A wallet list couldn't be retrieved because of an incorrect request parameter");
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        } catch (Exception e) {
            log.warn("A wallet list couldn't be retrieved because of a server error");
            return new ResponseEntity<>(HttpStatusCode.valueOf(500));
        }
    }

}
