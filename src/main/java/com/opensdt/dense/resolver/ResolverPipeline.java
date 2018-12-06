package com.opensdt.dense.resolver;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import io.netty.handler.codec.dns.DnsRecord;

public class ResolverPipeline {

    protected ObjectArrayList<Resolver> resolverList = new ObjectArrayList<>();

    /**
     * resolve domain name
     *
     * @param question dns question
     *
     * @return result ANSWER dns record or null, if domain coulnd't be resolved (e.q. if it doesn't exists in this resolver)
     */
    public DnsRecord resolve(DnsRecord question) {
        // try all resolvers and return the first one which can handle the request
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
