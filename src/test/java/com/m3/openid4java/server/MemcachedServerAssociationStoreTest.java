package com.m3.openid4java.server;

import org.junit.Test;
import org.openid4java.association.Association;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MemcachedServerAssociationStoreTest {

    String namespace = "MemcachedServerAssociationStoreTest";
    List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));

    @Test
    public void type() throws Exception {
        assertThat(MemcachedServerAssociationStore.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);
        assertThat(store, notNullValue());
    }

    @Test
    public void toKey_A$String() throws Exception {
        // given
        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);
        // when
        String handle = "xxxx";
        String actual = store.toKey(handle);
        // then
        String expected = "openid4java::MemcachedServerAssociationStoreTest::xxxx";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void findAssociation_A$String() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String handle = "find_A$String";

        try {
            store.remove(handle);

            // should be not found
            assertThat(store.findAssociation(handle), is(nullValue()));

            String type = Association.TYPE_HMAC_SHA1;
            String macKey = "xcxf23232sdfsdf";
            Date datetimeToExpire = new Date(System.currentTimeMillis() + 1000L);
            ServerAssociation serverAssociation = new ServerAssociation(handle, type, macKey, datetimeToExpire);
            store.saveAssociation(serverAssociation);

            // should be found
            assertThat(store.findAssociation(handle), is(not(nullValue())));

            Thread.sleep(2000L);

            // should not be found
            assertThat(store.findAssociation(handle), is(nullValue()));

        } finally {
            store.remove(handle);
        }
    }

    @Test
    public void saveAssociation_A$ServerAssociation() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String handle = "save_A$ServerAssociation";
        String type = Association.TYPE_HMAC_SHA1;
        String macKey = "xcxf23232sdfsdf";
        Date datetimeToExpire = new Date(System.currentTimeMillis() + 10000L);
        ServerAssociation serverAssociation = new ServerAssociation(handle, type, macKey, datetimeToExpire);
        store.saveAssociation(serverAssociation);

        // should be found
        assertThat(store.findAssociation(handle), is(not(nullValue())));
    }

    @Test
    public void deleteAssociation_A$ServerAssociation() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String handle = "delete_A$ServerAssociation";
        String type = Association.TYPE_HMAC_SHA1;
        String macKey = "xcxf23232sdfsdf";
        Date datetimeToExpire = new Date(System.currentTimeMillis() + 10000L);
        ServerAssociation serverAssociation = new ServerAssociation(handle, type, macKey, datetimeToExpire);
        store.saveAssociation(serverAssociation);

        // should be found
        assertThat(store.findAssociation(handle), is(not(nullValue())));

        store.deleteAssociation(serverAssociation);

        // should be not found
        assertThat(store.findAssociation(handle), is(nullValue()));
    }

    @Test
    public void generate_A$String$int() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String type = Association.TYPE_HMAC_SHA1;
        int expiryIn = 10;

        Association assoc = store.generate(type, expiryIn);
        assertThat(assoc.getHandle(), is(not(nullValue())));
        assertThat(assoc.getMacKey(), is(not(nullValue())));
        assertThat(assoc.getExpiry().getTime(), is(greaterThan(System.currentTimeMillis())));
        assertThat(assoc.getType(), is(equalTo(type)));
    }

    @Test
    public void load_A$String() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String type = Association.TYPE_HMAC_SHA1;
        int expiryIn = 10;
        Association generated = store.generate(type, expiryIn);

        Association assoc = store.load(generated.getHandle());

        assertThat(assoc.getHandle(), is(not(nullValue())));
        assertThat(assoc.getMacKey(), is(not(nullValue())));
        assertThat(assoc.getExpiry().getTime(), is(greaterThan(System.currentTimeMillis())));
        assertThat(assoc.getType(), is(equalTo(type)));
    }

    @Test
    public void remove_A$String() throws Exception {

        MemcachedServerAssociationStore store = new MemcachedServerAssociationStore(namespace, addresses);

        String type = Association.TYPE_HMAC_SHA1;
        int expiryIn = 360;
        Association generated = store.generate(type, expiryIn);

        store.remove(generated.getHandle());

        assertThat(store.load(generated.getHandle()), is(nullValue()));
    }

}
