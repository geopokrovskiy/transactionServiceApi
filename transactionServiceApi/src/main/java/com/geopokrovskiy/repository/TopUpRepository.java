package com.geopokrovskiy.repository;

import com.geopokrovskiy.entity.payment_request.TopUpRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopUpRepository extends JpaRepository<TopUpRequestEntity, UUID> {

}
