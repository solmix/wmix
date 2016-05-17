package org.solmix.wmix.exchange;

import java.util.SortedSet;

import org.solmix.commons.collections.SortedArraySet;
import org.solmix.exchange.interceptor.Interceptor;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.runtime.Extension;

/**
 * {@link Interceptor} phase定义。
 * <pre>
 * 
 * </pre>
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月9日
 */
@Extension(name="wmix")
public class WmixPhasePolicy implements PhasePolicy
{
    private SortedSet<Phase> in;
    private SortedSet<Phase> out;
    public WmixPhasePolicy(){
        createIn();
        createOut();
    }
    public WmixPhasePolicy(SortedSet<Phase> in,SortedSet<Phase> out){
        this.in=in;
        this.out=out;
    }
    /**
     * 
     * 
     */
    @Override
    public SortedSet<Phase> getInPhases() {
        return in;
    }

    @Override
    public SortedSet<Phase> getOutPhases() {
        return out;
    }
    /**
     * 
     */
    private void createOut() {
       out=new SortedArraySet<Phase>();
       int i=0;
       out.add(new Phase(Phase.SETUP, ++i*1000));
       
       out.add(new Phase(Phase.PRE_LOGICAL, ++i*1000));
       out.add(new Phase(Phase.USER_LOGICAL, ++i*1000));
       out.add(new Phase(Phase.POST_LOGICAL, ++i*1000));
       
       out.add(new Phase(Phase.PREPARE_SEND, ++i*1000));
       out.add(new Phase(Phase.PRE_STREAM, ++i*1000));
       
       out.add(new Phase(Phase.WRITE, ++i*1000));
       
       out.add(new Phase(Phase.PRE_MARSHAL, ++i*1000));
       out.add(new Phase(Phase.MARSHAL, ++i*1000));
       out.add(new Phase(Phase.POST_MARSHAL, ++i*1000));
       
       out.add(new Phase(Phase.USER_STREAM, ++i*1000));
       out.add(new Phase(Phase.POST_STREAM, ++i*1000));
       out.add(new Phase(Phase.SEND, ++i*1000));
       out.add(new Phase(Phase.SEND_ENDING, ++i*1000));
       out.add(new Phase(Phase.ENCODE_MARSHAL, ++i*1000));
       
       out.add(new Phase(Phase.WRITE_ENDING, ++i*1000));
       out.add(new Phase(Phase.PRE_STREAM_ENDING, ++i*1000));
       
       out.add(new Phase(Phase.PREPARE_SEND_ENDING, ++i * 1000));
       out.add(new Phase(Phase.POST_LOGICAL_ENDING, ++i * 1000));
       out.add(new Phase(Phase.USER_LOGICAL_ENDING, ++i * 1000));
       out.add(new Phase(Phase.PRE_LOGICAL_ENDING, ++i * 1000));
       out.add(new Phase(Phase.SETUP_ENDING, ++i * 1000));
    }
    /**
     * 
     */
    private void createIn() {
        in = new SortedArraySet<Phase>();
        int i=0;
        in.add(new Phase(Phase.PRE_UNMARSHAL, ++i*1000));
        in.add(new Phase(Phase.UNMARSHAL, ++i*1000));
        in.add(new Phase(Phase.POST_UNMARSHAL, ++i*1000));
        
        in.add(new Phase(Phase.PRE_LOGICAL, ++i*1000));
        in.add(new Phase(Phase.USER_LOGICAL, ++i*1000));
        in.add(new Phase(Phase.POST_LOGICAL, ++i*1000));
        
        in.add(new Phase(Phase.PRE_INVOKE, ++i*1000));
        in.add(new Phase(Phase.INVOKE, ++i*1000));
        in.add(new Phase(Phase.POST_INVOKE, ++i*1000));
    }
}
