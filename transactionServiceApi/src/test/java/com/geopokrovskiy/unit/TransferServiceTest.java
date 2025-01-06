package com.geopokrovskiy.unit;

import com.geopokrovskiy.entity.payment_request.PaymentRequestEntity;
import com.geopokrovskiy.entity.payment_request.TransferRequestEntity;
import com.geopokrovskiy.entity.status.Status;
import com.geopokrovskiy.repository.PaymentRequestRepository;
import com.geopokrovskiy.repository.TransferRepository;
import com.geopokrovskiy.repository.WalletRepository;
import com.geopokrovskiy.repository.WalletTypeRepository;
import com.geopokrovskiy.service.PaymentRequestService;
import com.geopokrovskiy.service.TransferService;
import com.geopokrovskiy.utils.ShardUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransferServiceTest {
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private PaymentRequestRepository paymentRequestRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletTypeRepository walletTypeRepository;
    @InjectMocks
    private TransferService transferService;
    @InjectMocks
    private PaymentRequestService paymentRequestService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewTransferOneShard() {

        TransferRequestEntity transferRequestEntity = UnitTestUtils.getValidTransferRequestOneShard_withoutId();
        UUID userFromId = transferRequestEntity.getUserFromId();
        UUID userToId = transferRequestEntity.getUserToId();


        PaymentRequestEntity paymentRequestEntityFrom = mock(PaymentRequestEntity.class);
        PaymentRequestEntity paymentRequestEntityTo = mock(PaymentRequestEntity.class);

       // when(ShardUtils.determineShard(userFromId)).thenReturn("shard1");
       // when(ShardUtils.determineShard(userToId)).thenReturn("shard1");

        when(paymentRequestService.addNewPaymentRequest(any(PaymentRequestEntity.class)))
                .thenAnswer((Answer<PaymentRequestEntity>) invocation -> invocation.getArgument(0));

        when(transferRepository.save(any(TransferRequestEntity.class)))
                .thenAnswer((Answer<TransferRequestEntity>) invocation -> {
                    TransferRequestEntity entity = invocation.getArgument(0);
                    entity.setUid(UUID.randomUUID());
                    return entity;
                });

        // Act
        TransferRequestEntity result = transferService.addNewTransfer(transferRequestEntity);

        // Assert
        assertNotNull(result.getUid());
        assertSame(Status.ACTIVE, result.getStatus());
        verify(paymentRequestService, times(2)).addNewPaymentRequest(any(PaymentRequestEntity.class));
        verify(transferRepository, times(1)).save(any(TransferRequestEntity.class));
    }
}
