
package org.solmix.wmix.exchange;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.model.NamedID;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.exchange.support.AbstractProtocolFactory;

public class WmixProtocolFactory extends AbstractProtocolFactory
{

    public static final String WMIX_PROTOCOL_ID = "http://solmix.org/wmix/protocol/wmix";

    public WmixProtocolFactory()
    {
    }

    public WmixProtocolFactory(Container container)
    {
        super(container);
    }

    @Override
    public void addListener(Transporter t, Endpoint e) {
        
    }
    @Override
    public Protocol createProtocol(ProtocolInfo info) {
        WmixProtocol ptl = new WmixProtocol(info);

        return ptl;
    }

    public ProtocolInfo createProtocolInfo(Service service, String protocol, Object configObject) {
        ProtocolInfo ptlInfo = new ProtocolInfo(null, WMIX_PROTOCOL_ID);
        ptlInfo.setName(new NamedID(WMIX_PROTOCOL_ID, "protocol"));
        return ptlInfo;
    }
}
