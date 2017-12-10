/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.sap.rfc;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

import com.sap.conn.jco.factory.JCoConnexion;

/**
 * The sap component integrates with the SAP using RCF transports.
 */
@UriEndpoint(firstVersion = "2.20.0", scheme = "sap", title = "SAP RFC", syntax = "sap:destination", producerOnly = true, label = "sap")
public class SapEndpoint extends DefaultEndpoint {

    private String remaining;

    @UriPath @Metadata(required = "true")
    private String partnerType = "LS";
    @UriPath @Metadata(required = "true")
    private String partnerNumber = "EXCHANGE";

    @UriParam @Metadata(required = "true", secret = true)
    private String username;
    @UriParam @Metadata(required = "true", secret = true)
    private String password;

    public SapEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new SapProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new SapConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Username for account.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Password for account.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public String getRemaining() {
        return this.remaining;
    }

    public JCoConnexion getConnexion() {
        return null;
    }
}
