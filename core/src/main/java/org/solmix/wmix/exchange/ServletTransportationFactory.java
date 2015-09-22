
package org.solmix.wmix.exchange;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.solmix.commons.util.Assert;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.PipelineFactory;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.TransporterFactory;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.support.TypeDetectSupport;
import org.solmix.runtime.Container;
import org.solmix.runtime.Extension;
import org.solmix.runtime.bean.BeanConfigurer;

@Extension(name = " http://www.solmix.org/wmix")
public class ServletTransportationFactory implements PipelineFactory, TransporterFactory, TypeDetectSupport
{

    private static final Set<String> URI_PREFIXES = new HashSet<String>();
    static {
        URI_PREFIXES.add("http://");
        URI_PREFIXES.add("https://");
    }

    public static final String DEFAULT_TRANSPORT_ID= "http://www.solmix.org/wmix";
    public static final List<String> TRANSPORT_TYPES = Arrays.asList(DEFAULT_TRANSPORT_ID);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock r = lock.readLock();

    @Override
    public Transporter getTransporter(EndpointInfo ei, Container container) throws IOException {
        Assert.assertNotNull(ei, "EndpointInfo not null");
            r.lock();
        try {
            return new ServletTransporter(container,ei,ei.getAddress());
        } finally {
            r.unlock();
        }
    }

    @Override
    public Pipeline getPipeline(EndpointInfo info, Container c) throws IOException {
        return getPipeline(info, info.getAddress(), c);
    }

    @Override
    public Pipeline getPipeline(EndpointInfo info, String address, Container c) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getUriPrefixes() {
        return URI_PREFIXES;
    }

    @Override
    public List<String> getTransportTypes() {
        return TRANSPORT_TYPES;
    }

    protected void configure(Container c, Object bean) {
        configure(c, bean, null, null);
    }

    protected void configure(Container c, Object bean, String name, String extraName) {
        BeanConfigurer configurer = c.getExtension(BeanConfigurer.class);
        if (null != configurer) {
            configurer.configureBean(name, bean);
            if (extraName != null) {
                configurer.configureBean(extraName, bean);
            }
        }
    }

}
