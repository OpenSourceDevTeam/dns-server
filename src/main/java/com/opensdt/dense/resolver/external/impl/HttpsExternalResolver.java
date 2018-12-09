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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensdt.dense.resolver.external.ExternalResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.dns.DnsRecordType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import sun.net.util.IPAddressUtil;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class HttpsExternalResolver implements ExternalResolver {

    private HttpClient httpClient;

    private String url;

    public HttpsExternalResolver(String url) {
        this.httpClient = HttpClients.custom()
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setConnectionManager(new PoolingHttpClientConnectionManager(5, TimeUnit.MINUTES))
                .build();
        this.url = url;
    }

    @Override
    public byte[] resolve(String hostname, DnsRecordType dnsRecordType) {
        HttpGet get = new HttpGet(String.format(url, hostname, dnsRecordType.name()));
        get.setHeader("Accept", "application/dns-json");

        try {
            HttpResponse response = httpClient.execute(get);

            String json = EntityUtils.toString(response.getEntity());

            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

            // TODO: TTL
            JsonObject answer = jsonObject.getAsJsonArray("Answer").get(0).getAsJsonObject();

            String ip = answer.get("data").getAsString();

            if (dnsRecordType == DnsRecordType.MX) {
                return parseMX(ip);
            } else if (dnsRecordType == DnsRecordType.A) {
                return parseV4(ip);
            }

            return parseV6(ip);
        } catch (IOException e) {
            return null;
        }
    }

    private byte[] parseMX(String name) {
        if (name.equals(".")) {
            return new byte[] { 0 };
        }

        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();

        String[] priorityAndName = name.split(" ");

        byteBuf.writeShort(Short.parseShort(priorityAndName[0]));

        String[] labels = priorityAndName[1].split("\\.");
        for (String label : labels) {
            int labelLength = label.length();
            if (labelLength == 0) {
                break;
            }

            byteBuf.writeByte(labelLength);
            ByteBufUtil.writeAscii(byteBuf, label);
        }

        byteBuf.writeByte(0);

        byte[] encoded = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(encoded);

        return encoded;
    }

    private byte[] parseV4(String ip) {
        byte[] bytes = new byte[4];

        StringTokenizer tokenizer = new StringTokenizer(ip, "\\.");
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            bytes[i] = (byte) (Integer.parseInt(tokenizer.nextToken()) & 0xFF);
        }

        return bytes;
    }

    private byte[] parseV6(String ip) {
        // TODO: Custom impl without usage of sun package
        return IPAddressUtil.textToNumericFormatV6(ip);
    }
}
