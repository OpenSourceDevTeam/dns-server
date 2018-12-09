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

package com.opensdt.dense.resolver.impl;

import com.opensdt.dense.resolver.Resolver;
import com.opensdt.dense.resolver.external.ExternalResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;

public class GlobalResolver implements Resolver {

    private ExternalResolver externalResolver;

    public GlobalResolver(com.opensdt.dense.resolver.external.ExternalResolver externalResolver) {
        this.externalResolver = externalResolver;
    }

    @Override
    public DnsRecord resolve(DnsRecord question) {
        byte[] address = this.externalResolver.resolve(question.name(), question.type());
        if (address == null) {
            return null;
        }

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(address);

        // TODO: TTL
        return new DefaultDnsRawRecord(question.name(), question.type(), 30, buffer);
    }
}
