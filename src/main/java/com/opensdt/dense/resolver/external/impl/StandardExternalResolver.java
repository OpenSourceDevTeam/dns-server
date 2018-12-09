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

package com.opensdt.dense.resolver.external.impl;

import com.opensdt.dense.channel.ChannelUtils;
import com.opensdt.dense.resolver.external.ExternalResolver;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.SingletonDnsServerAddressStreamProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class StandardExternalResolver implements ExternalResolver {

    private static Logger logger = LoggerFactory.getLogger(StandardExternalResolver.class);

    private DnsNameResolver nameResolver;

    public StandardExternalResolver(InetSocketAddress address) {
        nameResolver = new DnsNameResolverBuilder()
                .eventLoop(ChannelUtils.getEventLoopGroup(1).next())
                .channelType(ChannelUtils.getChannel())
                .nameServerProvider(new SingletonDnsServerAddressStreamProvider(address))
                .build();
    }

    @Override
    public byte[] resolve(String hostname, DnsRecordType dnsRecordType) {
        try {
            // TODO: Record type
            return this.nameResolver.resolve(hostname).get().getAddress();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while resolving hostname " + hostname, e);
        }

        return null;
    }
}
