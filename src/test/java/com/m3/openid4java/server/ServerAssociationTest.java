package com.m3.openid4java.server;

import org.junit.Test;
import org.openid4java.association.Association;

import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ServerAssociationTest {

    @Test
    public void type() throws Exception {
        assertThat(ServerAssociation.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        String handle = "xxx";
        String type = Association.TYPE_HMAC_SHA1;
        String macKey = "xcxf23232sdfsdf";
        Date datetimeToExpire = new Date();
        ServerAssociation serverAssociation = new ServerAssociation(handle, type, macKey, datetimeToExpire);
        assertThat(serverAssociation, notNullValue());
    }

}
