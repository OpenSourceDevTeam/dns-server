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
import com.opensdt.dense.config.Config;
import com.opensdt.dense.pipeline.DenseChannelInitializer;
import com.opensdt.dense.resolver.ResolverPipeline;
import com.opensdt.dense.resolver.external.impl.StandardExternalResolver;
import com.opensdt.dense.resolver.impl.GlobalResolver;
import com.opensdt.dense.resolver.impl.LocalHostsResolver;
import com.opensdt.dense.resolver.impl.LocalZoneResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class DenseBootstrap {

    private static Logger logger = LoggerFactory.getLogger(DenseBootstrap.class);

    public static void main(String[] args) {
        logger.info("Bootstrapping dense DNS Server");

        ResolverPipeline resolverPipeline = new ResolverPipeline();
        resolverPipeline.addResolver(new LocalHostsResolver());
        resolverPipeline.addResolver(new LocalZoneResolver());

        InetSocketAddress address = new InetSocketAddress("1.1.1.1", 53);
        StandardExternalResolver externalResolver = new StandardExternalResolver(address);
        resolverPipeline.addResolver(new GlobalResolver(externalResolver));

        EventLoopGroup boss = ChannelUtils.getEventLoopGroup(1);
        try {
            new Bootstrap()
                    .group(boss)
                    .channel(ChannelUtils.getChannel())
                    .handler(new DenseChannelInitializer(resolverPipeline))
                    .bind(Config.getString("ip", "localhost"), Config.getInteger("port", 53))
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
