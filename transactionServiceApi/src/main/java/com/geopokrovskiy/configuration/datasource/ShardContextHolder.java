package com.geopokrovskiy.configuration.datasource;

public class ShardContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentShard(String shard) {
        contextHolder.set(shard);
    }

    public static String getCurrentShard() {
        return contextHolder.get();
    }

    public static void clearShard() {
        contextHolder.remove();
    }
}
