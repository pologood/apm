
package com.baidu.oped.apm.profiler.modifier.connector.httpclient3;

import java.security.ProtectionDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.oped.apm.bootstrap.Agent;
import com.baidu.oped.apm.bootstrap.instrument.ByteCodeInstrumentor;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentClass;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentException;
import com.baidu.oped.apm.bootstrap.interceptor.Interceptor;
import com.baidu.oped.apm.profiler.modifier.AbstractModifier;

/**
 * class HttpClientModifier 
 *
 * @author meidongxu@baidu.com
 */
public class HttpClientModifier extends AbstractModifier {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public HttpClientModifier(ByteCodeInstrumentor byteCodeInstrumentor, Agent agent) {
        super(byteCodeInstrumentor, agent);
    }

    @Override
    public byte[] modify(ClassLoader classLoader, String javassistClassName, ProtectionDomain protectedDomain, byte[] classFileBuffer) {
        if (logger.isInfoEnabled()) {
            logger.info("Modifing. {}", javassistClassName);
        }
        
        try {
            InstrumentClass aClass = byteCodeInstrumentor.getClass(classLoader, javassistClassName, classFileBuffer);
            addInterceptorForExecuteMethod(classLoader, protectedDomain, aClass);
            return aClass.toBytecode();
        } catch (Throwable e) {
            logger.warn("org.apache.commons.httpclient.HttpClient modifier error. Caused:{}", e.getMessage(), e);
            return null;
        }
    }

    private void addInterceptorForExecuteMethod(ClassLoader classLoader, ProtectionDomain protectedDomain, InstrumentClass aClass) throws InstrumentException {
        Interceptor interceptor = newExecuteInterceptor(classLoader, protectedDomain);
        aClass.addScopeInterceptorIfDeclared("executeMethod", new String[]{"org.apache.commons.httpclient.HttpMethod"}, interceptor, HttpClient3Scope.SCOPE);
        
        interceptor = newExecuteInterceptor(classLoader, protectedDomain);
        aClass.addScopeInterceptorIfDeclared("executeMethod", new String[]{"org.apache.commons.httpclient.HostConfiguration", "org.apache.commons.httpclient.HttpMethod"}, interceptor, HttpClient3Scope.SCOPE);
        
        interceptor = newExecuteInterceptor(classLoader, protectedDomain);
        aClass.addScopeInterceptorIfDeclared("executeMethod", new String[]{"org.apache.commons.httpclient.HostConfiguration", "org.apache.commons.httpclient.HttpMethod", "org.apache.commons.httpclient.HttpState"}, interceptor, HttpClient3Scope.SCOPE);
    }

    private Interceptor newExecuteInterceptor(ClassLoader classLoader, ProtectionDomain protectedDomain) throws InstrumentException {
        return byteCodeInstrumentor.newInterceptor(classLoader, protectedDomain, "com.baidu.oped.apm.profiler.modifier.connector.httpclient3.interceptor.ExecuteInterceptor");
    }

    @Override
    public String getTargetClass() {
        return "org/apache/commons/httpclient/HttpClient";
    }
}
