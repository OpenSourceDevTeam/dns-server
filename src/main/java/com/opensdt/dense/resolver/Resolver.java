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
