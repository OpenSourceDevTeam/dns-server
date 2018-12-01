/*
 * Copyright (c) 2018 OpenSourceDevTeam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensdt.dense;

import com.opensdt.dense.channel.ChannelUtils;
import com.opensdt.dense.pipeline.DenseChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DenseBootstrap {

    private static Logger logger = LoggerFactory.getLogger(DenseBootstrap.class);

    public static void main(String[] args) {
        logger.info("Bootstrapping dense DNS Server");

        EventLoopGroup boss = ChannelUtils.getEventLoopGroup(1);
        try {
            new Bootstrap()
                    .group(boss)
                    .channel(ChannelUtils.getChannel())
                    .handler(new DenseChannelInitializer())
                    // TODO: Configurable bind IP
                    .bind(53)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            boss.shutdownGracefully();
        }
    }
}
