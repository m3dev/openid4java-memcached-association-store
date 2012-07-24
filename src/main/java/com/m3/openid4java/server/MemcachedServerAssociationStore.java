/*
 * Copyright 2012 M3, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.m3.openid4java.server;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import org.apache.commons.codec.binary.Base64;
import org.openid4java.association.Association;
import org.openid4java.association.AssociationException;
import org.openid4java.server.ServerAssociationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MemcachedServerAssociationStore implements ServerAssociationStore {

    private static Logger log = LoggerFactory.getLogger(MemcachedServerAssociationStore.class);

    private final Random random = new Random(System.currentTimeMillis());

    private final String namespace;
    private final MemcachedClient memcached;

    public MemcachedServerAssociationStore(String namespace, List<InetSocketAddress> addresses) throws IOException {
        this.namespace = namespace;
        this.memcached = new XMemcachedClient(addresses);
    }

    String toKey(String handle) {
        return "openid4java::" + namespace + "::" + handle;
    }

    ServerAssociation find(String handle) throws Exception {
        return (ServerAssociation) memcached.get(toKey(handle));
    }

    void save(ServerAssociation serverAssociation) throws Exception {
        Long expiration = serverAssociation.getDatetimeToExpire().getTime() - System.currentTimeMillis();
        memcached.set(toKey(serverAssociation.getHandle()), expiration.intValue(), serverAssociation);
    }

    void delete(ServerAssociation serverAssociation) throws Exception {
        memcached.delete(toKey(serverAssociation.getHandle()));
    }

    @Override
    public Association generate(String type, int expiryIn) throws AssociationException {
        int attemptsLeft = 5;
        while (attemptsLeft > 0) {
            try {
                String handle = Long.toHexString(random.nextLong());
                Association association = Association.generate(type, handle, expiryIn);
                String macKey = new String(Base64.encodeBase64(association.getMacKey().getEncoded()));
                save(new ServerAssociation(handle, type, macKey, association.getExpiry()));
                return association;
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to save a new association value.", e);
                }
                attemptsLeft--;
            }
        }
        throw new AssociationException("Error generating association.");
    }

    @Override
    public Association load(String handle) {

        Association association = null;

        try {
            ServerAssociation serverAssociation = find(handle);

            if (serverAssociation == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Association is not found. (handle: " + handle + ")");
                }
                return null;
            }

            byte[] macKeyBytes = Base64.decodeBase64(serverAssociation.getMacKey().getBytes());
            Date datetimeToExpire = serverAssociation.getDatetimeToExpire();

            String type = serverAssociation.getType();
            if (type == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Null association type retrieved from memcached");
                }
            } else if (type.equals(Association.TYPE_HMAC_SHA1)) {
                association = Association.createHmacSha1(handle, macKeyBytes, datetimeToExpire);
            } else if (type.equals(Association.TYPE_HMAC_SHA256)) {
                association = Association.createHmacSha256(handle, macKeyBytes, datetimeToExpire);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid association type retrieved from memcached: " + type);
                }
            }

        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to load an association value.", e);
            }
            throw new RuntimeException(e);
        }
        return association;
    }

    @Override
    public void remove(String handle) {
        try {
            ServerAssociation serverAssociation = find(handle);
            if (serverAssociation != null) {
                delete(serverAssociation);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to delete an association value.", e);
            }
            throw new RuntimeException(e);
        }
    }

}
