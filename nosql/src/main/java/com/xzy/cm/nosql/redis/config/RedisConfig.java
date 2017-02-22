package com.xzy.cm.nosql.redis.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Created by xiongzhanyuan on 2017/2/7.
 */

public abstract class RedisConfig {
    @Value("${redis.nodes}")
    protected String nodes;

    protected RedissonClient redisson;

    @PostConstruct
    public void init() {
        Config config = new Config();
        if (StringUtils.isNotBlank(nodes)) {
            String [] nodesArray = nodes.split(",");
            if (nodesArray.length > 1) {
                ClusterServersConfig cs_cfg = config.useClusterServers();
                for (String node : nodesArray) {
                    cs_cfg.addNodeAddress(node);
                }
            } else {
                SingleServerConfig ss_cfg = config.useSingleServer();
                ss_cfg.setAddress(nodes);
            }

        }
        config.setCodec(new StringCodec());

        redisson = Redisson.create(config);
    }
}
