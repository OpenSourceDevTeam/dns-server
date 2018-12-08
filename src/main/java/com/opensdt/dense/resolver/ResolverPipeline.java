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

package com.opensdt.dense.resolver;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import io.netty.handler.codec.dns.DnsRecord;

public class ResolverPipeline {

    private ObjectArrayList<Resolver> resolverList = new ObjectArrayList<>();

    /**
     * Resolves the domain queried in the question section.
     *
     * @param question The DNS question section record.
     *
     * @return Returns the DNS answer section record or null, if the domain couldn't be resolved (e.q. if it doesn't exists in this resolver).
     */
    public DnsRecord resolve(DnsRecord question) {
        for (ObjectCursor<Resolver> cursor : this.resolverList) {
            DnsRecord result = cursor.value.resolve(question);

            if (result == null) {
                continue;
            }

            return result;
        }

        return null;
    }

    public void addResolver(Resolver resolver) {
        this.resolverList.add(resolver);
    }

    public void removeResolver(Resolver resolver) {
        this.resolverList.removeFirst(resolver);
    }

}
