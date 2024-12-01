package com.geopokrovskiy.utils;

import java.util.UUID;

public class ShardUtils {

    public static String determineShard(UUID userUid) {
        int shardNumber = Math.abs(userUid.hashCode()) % 2;
        return shardNumber == 0 ? "shard1" : "shard2";
    }
}
