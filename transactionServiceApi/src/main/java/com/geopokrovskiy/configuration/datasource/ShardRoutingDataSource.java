package com.geopokrovskiy.configuration.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class ShardRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String shard = ShardContextHolder.getCurrentShard();
        if (shard == null) {
            shard = "shard1";
        }
        log.info("Current shard is {}", shard);
        return shard;
    }
}


