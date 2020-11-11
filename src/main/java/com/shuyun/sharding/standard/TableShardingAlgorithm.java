package com.shuyun.sharding.standard;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class TableShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        int jobId = (int) shardingValue.getValue();
        int tableIndex = jobId % 5;
        return shardingValue.getLogicTableName() + "_0" + tableIndex;
    }

}
