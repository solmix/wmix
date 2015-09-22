
package org.solmix.wmix.exchange;

import java.util.Dictionary;

import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Protocol;
import org.solmix.exchange.Service;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ProtocolInfo;
import org.solmix.exchange.processor.InChainInitProcessor;
import org.solmix.exchange.support.AbstractProtocolFactory;
import org.solmix.runtime.Container;

public class WmixProtocolFactory extends AbstractProtocolFactory
{

    public static final String WMIX_PROTOCOL_ID = "http://www.solmix.org/wmix";

    public WmixProtocolFactory()
    {
    }

    public WmixProtocolFactory(Container container)
    {
        super(container);
    }

    @Override
    public void addListener(Transporter t, Endpoint e) {
        InChainInitProcessor cip = new InChainInitProcessor(e, container);
        t.setProcessor(cip);
    }
    
    @Override
    public Protocol createProtocol(ProtocolInfo info) {
        WmixProtocol ptl = new WmixProtocol(info);
        return ptl;
    }

    @Override
    public ProtocolInfo createProtocolInfo(Service service, String protocol, Dictionary<String, ?> configObject) {
        ProtocolInfo ptlInfo = new ProtocolInfo(null, WMIX_PROTOCOL_ID);
        ptlInfo.setName(new NamedID(WMIX_PROTOCOL_ID, "protocol"));
        return ptlInfo;
    }
}
