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

import io.netty.handler.codec.dns.DnsRecord;

public interface Resolver {

    /**
     * Resolves the domain queried in the question section.
     *
     * @param question The DNS question section record.
     *
     * @return Returns the DNS answer section record or null, if the domain couldn't be resolved (e.q. if it doesn't exists in this resolver).
    */
    DnsRecord resolve(DnsRecord question);
}
