
package com.baidu.oped.apm.profiler.modifier.spring.beans;

import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import javassist.bytecode.AccessFlag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.oped.apm.bootstrap.instrument.ByteCodeInstrumentor;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentClass;
import com.baidu.oped.apm.bootstrap.instrument.MethodFilter;
import com.baidu.oped.apm.bootstrap.instrument.MethodInfo;
import com.baidu.oped.apm.common.ServiceType;
import com.baidu.oped.apm.profiler.modifier.Modifier;
import com.baidu.oped.apm.profiler.modifier.method.interceptor.MethodInterceptor;

/**
 * class BeanMethodModifier 
 *
 * @author meidongxu@baidu.com
 */
public class BeanMethodModifier implements Modifier {
    static final MethodFilter METHOD_FILTER = new MethodFilter() {
        private static final int REQUIRED_ACCESS_FLAG = AccessFlag.PUBLIC;
        private static final int REJECTED_ACCESS_FLAG = AccessFlag.ABSTRACT | AccessFlag.BRIDGE | AccessFlag.NATIVE | AccessFlag.PRIVATE |
                AccessFlag.PROTECTED | AccessFlag.SYNTHETIC | AccessFlag.STATIC;

        @Override
        public boolean filter(MethodInfo ctMethod) {
            if (ctMethod.isConstructor()) {
                return false;
            }

            int access = ctMethod.getModifiers();

            return ((access & REQUIRED_ACCESS_FLAG) == 0) || ((access & REJECTED_ACCESS_FLAG) != 0);
        }
    };

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ByteCodeInstrumentor byteCodeInstrumentor;

    public BeanMethodModifier(ByteCodeInstrumentor byteCodeInstrumentor) {
        this.byteCodeInstrumentor = byteCodeInstrumentor;
    }

    public byte[] modify(ClassLoader classLoader, String javassistClassName, ProtectionDomain protectedDomain, byte[] classFileBuffer) {
        if (logger.isInfoEnabled()) {
            logger.info("Modify {}", javassistClassName);
        }


        try {
            InstrumentClass clazz = byteCodeInstrumentor.getClass(classLoader, javassistClassName, classFileBuffer);

            if (!clazz.isInterceptable()) {
                return null;
            }

            List<MethodInfo> methodList = clazz.getDeclaredMethods(METHOD_FILTER);
            for (MethodInfo method : methodList) {
                if (logger.isTraceEnabled()) {
                    logger.trace("### c={}, m={}, params={}", javassistClassName, method.getName(), Arrays.toString(method.getParameterTypes()));
                }

                MethodInterceptor interceptor = new MethodInterceptor();
                interceptor.setServiceType(ServiceType.SPRING_BEAN);
                
                clazz.addInterceptor(method.getName(), method.getParameterTypes(), interceptor);
            }

            return clazz.toBytecode();
        } catch (Exception e) {
            logger.warn("modify fail. Cause:{}", e.getMessage(), e);
            return null;
        }
    }
}