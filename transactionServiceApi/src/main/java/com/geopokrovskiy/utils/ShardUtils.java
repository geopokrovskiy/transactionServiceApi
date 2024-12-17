package com.geopokrovskiy.utils;

import com.geopokrovskiy.configuration.datasource.ShardContextHolder;

import java.util.UUID;

public class ShardUtils {

    public static String determineShard(UUID userUid) {
        int shardNumber = Math.abs(userUid.hashCode()) % 2;
        return shardNumber == 0 ? "shard1" : "shard2";
    }

    public static void setShard(UUID userId) {
        String shard = determineShard(userId);
        ShardContextHolder.setCurrentShard(shard);
    }
}
