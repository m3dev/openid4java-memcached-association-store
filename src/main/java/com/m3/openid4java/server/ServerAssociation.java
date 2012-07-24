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

import java.io.Serializable;
import java.util.Date;

public class ServerAssociation implements Serializable {

    private String handle;
    private String type;
    private String macKey;
    private Date datetimeToExpire;

    public ServerAssociation() {
    }

    public ServerAssociation(String handle, String type, String macKey, Date datetimeToExpire) {
        this.handle = handle;
        this.type = type;
        this.macKey = macKey;
        this.datetimeToExpire = datetimeToExpire;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMacKey() {
        return macKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public Date getDatetimeToExpire() {
        return datetimeToExpire;
    }

    public void setDatetimeToExpire(Date datetimeToExpire) {
        this.datetimeToExpire = datetimeToExpire;
    }

    @Override
    public String toString() {
        return "ServerAssociation(handle: " + getHandle() + ", macKey: " + getMacKey()
                + ", type: " + getType() + ", datetimeToExpire: " + getDatetimeToExpire() + ")";
    }

}
