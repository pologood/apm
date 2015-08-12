
package com.baidu.oped.apm.profiler.modifier.redis;

import java.security.ProtectionDomain;

import com.baidu.oped.apm.bootstrap.Agent;
import com.baidu.oped.apm.bootstrap.instrument.ByteCodeInstrumentor;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentClass;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentException;
import com.baidu.oped.apm.bootstrap.instrument.MethodInfo;
import com.baidu.oped.apm.bootstrap.instrument.NotFoundInstrumentException;
import com.baidu.oped.apm.bootstrap.interceptor.Interceptor;
import com.baidu.oped.apm.bootstrap.interceptor.tracevalue.MapTraceValue;

/**
 * jedis(redis client) pipeline modifier
 * 
 * @author jaehong.kim
 *
 */
public class JedisPipelineModifier extends JedisPipelineBaseModifier {

    public JedisPipelineModifier(ByteCodeInstrumentor byteCodeInstrumentor, Agent agent) {
        super(byteCodeInstrumentor, agent);
    }

    @Override
    public String getTargetClass() {
        return "redis/clients/jedis/Pipeline";
    }

    @Override
    protected void beforeAddInterceptor(ClassLoader classLoader, ProtectionDomain protectedDomain, final InstrumentClass instrumentClass) throws NotFoundInstrumentException, InstrumentException {
        // for trace endPoint
        instrumentClass.addTraceValue(MapTraceValue.class);

        // add constructor interceptor
        addConstructorInterceptor(classLoader, protectedDomain, instrumentClass);
        
        // add method interceptor
        addSetClientMethodInterceptor(classLoader, protectedDomain, instrumentClass);
    }

    private void addSetClientMethodInterceptor(ClassLoader classLoader, ProtectionDomain protectedDomain, final InstrumentClass instrumentClass) throws NotFoundInstrumentException, InstrumentException {
        for (MethodInfo method : instrumentClass.getDeclaredMethods()) {
            if (method.getName().equals("setClient")) {
                // jedis 2.x
                final Interceptor methodInterceptor = byteCodeInstrumentor.newInterceptor(classLoader, protectedDomain, "com.baidu.oped.apm.profiler.modifier.redis.interceptor.JedisPipelineSetClientMethodInterceptor");
                instrumentClass.addInterceptor("setClient", new String[] { "redis.clients.jedis.Client" }, methodInterceptor);
            }
        }
    }

    private void addConstructorInterceptor(ClassLoader classLoader, ProtectionDomain protectedDomain, final InstrumentClass instrumentClass) throws InstrumentException {
        final Interceptor constructorInterceptor = byteCodeInstrumentor.newInterceptor(classLoader, protectedDomain, "com.baidu.oped.apm.profiler.modifier.redis.interceptor.JedisPipelineConstructorInterceptor");
        try {
            // jedis 1.x
            instrumentClass.addConstructorInterceptor(new String[] { "redis.clients.jedis.Client" }, constructorInterceptor);
        } catch (Exception e) {
            // backward compatibility error
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to add constructor interceptor(only jedis 1.x). caused={}", e.getMessage(), e);
            }
        }
    }
}