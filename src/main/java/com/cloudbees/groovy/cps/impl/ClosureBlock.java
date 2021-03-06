package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Block;
import com.cloudbees.groovy.cps.Continuation;
import com.cloudbees.groovy.cps.Env;
import com.cloudbees.groovy.cps.Next;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Closure instantiation: { ... }
 *
 * @author Kohsuke Kawaguchi
 */
public class ClosureBlock implements Block {
    private final List<String> parameters;
    private final Block body;
    // this field would be null if we are deserializing from data saved by old version
    private final Class<? extends CpsClosure> closureType;
    private final SourceLocation loc;

    public ClosureBlock(SourceLocation loc, List<String> parameters, Block body, Class<? extends CpsClosure> closureType) {
        this.loc = loc;
        this.parameters = parameters;
        this.body = body;
        this.closureType = closureType;
    }

    private Class<? extends CpsClosure> closureType() {
        if (closureType==null)  return CpsClosure.class; // backward compatibility with persisted form
        return closureType;
    }

    public Next eval(Env e, Continuation k) {
        try {
            Constructor c = closureType().getConstructor(
                Object.class, Object.class, List.class, Block.class, Env.class
            );
            return k.receive(c.newInstance(e.closureOwner(), e.getLocalVariable("this"), parameters,body,e));
        } catch (Exception x) {
            return new ContinuationGroup() {}.
                throwException(e, x, loc, new ReferenceStackTrace());
        }
    }

    private static final long serialVersionUID = 1L;
}
