package org.solmix.wmix.condition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.Assert;
import org.solmix.exchange.Message;
import org.solmix.wmix.exchange.WmixMessage;


public class PathConditionTest
{

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_upload() {
        PathCondition p  = new PathCondition();
        p.setRule("^*.up$");
        Message message  = new WmixMessage();
        message.put(Message.PATH_INFO, "/data/aa.b.up");
        Assert.assertTrue(p.accept(message));
        
        
    }
    
    @Test
    public void test_image() {
        PathCondition p  = new PathCondition();
        p.setRule("^*.png$,^*.jpg$");
        Message message  = new WmixMessage();
        message.put(Message.PATH_INFO, "/data/aa.b.png");
        Assert.assertTrue(p.accept(message));
        message.put(Message.PATH_INFO, "/data/aa.b.jpg");
        Assert.assertTrue(p.accept(message));
        
        
    }

}
