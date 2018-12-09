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

import com.opensdt.dense.resolver.ResolverPipeline;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DnsChannelHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {

    private static Logger logger = LoggerFactory.getLogger(DnsChannelHandler.class);

    private ResolverPipeline resolverPipeline;

    public DnsChannelHandler(ResolverPipeline resolverPipeline) {
        this.resolverPipeline = resolverPipeline;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) throws Exception {
        DnsRecord question = query.recordAt(DnsSection.QUESTION);

        logger.info("Request: {} - {}", question.type(), question.name());

        // We currently only support A, AAAA and MX
        if (question.type() != DnsRecordType.A && question.type() != DnsRecordType.AAAA && question.type() != DnsRecordType.MX) {
            DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());
            response.setCode(DnsResponseCode.NOTIMP);
            ctx.writeAndFlush(response);
            return;
        }

        DnsRecord answer = this.resolverPipeline.resolve(question);

        DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());

        if (answer != null) {
            response.addRecord(DnsSection.ANSWER, answer);
        } else {
            // If we don't have an answer, return "Non-Existent Domain"
            response.setCode(DnsResponseCode.NXDOMAIN);
        }

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in channel handler", cause);

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
