package com.opensdt.dense.resolver;

import io.netty.handler.codec.dns.DnsRecord;

public interface Resolver {

    /**
     * resolve domain name
     *
     * @param question dns question
     *
     * @return result ANSWER dns record or null, if domain coulnd't be resolved (e.q. if it doesn't exists in this resolver)
    */
    public DnsRecord resolve(DnsRecord question);

}
