package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Block;
import com.cloudbees.groovy.cps.Continuation;
import com.cloudbees.groovy.cps.Env;
import com.cloudbees.groovy.cps.Next;

import java.util.List;

/**
 * Represents a CPS-transformed function.
 *
 * @author Kohsuke Kawaguchi
 */
public class CpsFunction extends CpsCallable {
    public CpsFunction(List<String> parameters, Block body) {
        super(parameters, body);
    }

    public Next invoke(Env caller, Object receiver, List<?> args, Continuation k) {
        Env e = new FunctionCallEnv(caller, receiver, k);
        assignArguments(args,e);
        return new Next(body, e, k);
    }
}