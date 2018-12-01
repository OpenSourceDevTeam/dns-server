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

package com.opensdt.dense.pipeline.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class DnsChannelHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {

    private static Logger logger = LoggerFactory.getLogger(DnsChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) throws Exception {
        DnsRecord question = query.recordAt(DnsSection.QUESTION);

        logger.info("Request: {} - {}", question.type(), question.name());

        // Test response for an A record
        if (question.type() == DnsRecordType.A) {
            DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());

            ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
            byte[] address = new InetSocketAddress("127.0.0.1", 0).getAddress().getAddress();
            // Write all 4 octets from the IP address
            buffer.writeBytes(address);

            response.addRecord(DnsSection.ANSWER, new DefaultDnsRawRecord(question.name(), DnsRecordType.A, 30, buffer));

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in channel handler", cause);

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
