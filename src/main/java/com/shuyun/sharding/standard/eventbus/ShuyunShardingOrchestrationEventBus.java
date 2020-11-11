package com.shuyun.sharding.standard.eventbus;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.SynchronousQueue;

@SuppressWarnings("all")
public class ShuyunShardingOrchestrationEventBus {

    private static final EventBus INSTANCE = new EventBus();

    public static EventBus getInstance() {
        return INSTANCE;
    }

}
