/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      sapRfc://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.sap.rfc;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ServiceHelper;
import org.apache.camel.util.URISupport;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class SapProducer extends DefaultProducer {

    private Producer sapRfc;

    public SapProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public SapEndpoint getEndpoint() {
        return SapEndpoint.class.cast(super.getEndpoint());
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (exchange.getIn().getBody() instanceof IDocDocument){
            IDocDocument idoc = exchange.getIn().getBody(IDocDocument.class);
            sendIdoc(idoc);
        } else if (exchange.getIn().getBody() instanceof IDocDocumentList){
            IDocDocumentList idocList = exchange.getIn().getBody(IDocDocumentList.class);
            sendIdocList(idocList);
        } else {
            String idoc = exchange.getIn().getBody(String.class);
            IDocXMLProcessor xmlProcessor = JCoIDoc.getIDocFactory().getIDocXMLProcessor();
            IDocDocumentList idocList = xmlProcessor.parse(JCoIDoc.getIDocRepository(getEndpoint().getConnexion().getDestination()), idoc);
            sendIdocList(idocList);
        }
    }

    private void sendIdocList(IDocDocumentList idocList) throws JCoException {
        JCoDestination destination = getEndpoint().getConnexion().getDestination();
        String tid = destination.createTID();
        if (null == tid) {
            throw new JCoException(1, "can not create transaction for " + getEndpoint().getConnexion().getDestinationName());
        }
        JCoIDoc.send(idocList, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
        destination.confirmTID(tid);
    }

    private void sendIdoc(IDocDocument idoc) throws JCoException {
        JCoDestination destination = getEndpoint().getConnexion().getDestination();
        String tid = destination.createTID();
        if (null == tid) {
            throw new JCoException(1, "can not create transaction for " + getEndpoint().getConnexion().getDestinationName());
        }
        JCoIDoc.send(idoc, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
        destination.confirmTID(tid);
    }

    @Override
    protected void doStart() throws Exception {
        String url = getEndpoint().getRemaining() + "?authUsername=" + getEndpoint().getUsername() + "&authPassword=" + getEndpoint().getPassword() + "&authMethod=Basic";
        if (log.isInfoEnabled()) {
            log.info("Creating NetWeaverProducer using url: {}", URISupport.sanitizeUri(url));
        }

        sapRfc = getEndpoint().getCamelContext().getEndpoint(url).createProducer();
        ServiceHelper.startService(sapRfc);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(sapRfc);
    }
}
