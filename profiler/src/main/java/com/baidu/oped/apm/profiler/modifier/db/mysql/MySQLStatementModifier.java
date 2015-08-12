
package com.baidu.oped.apm.profiler.modifier.db.mysql;

import java.security.ProtectionDomain;

import com.baidu.oped.apm.bootstrap.Agent;
import com.baidu.oped.apm.bootstrap.instrument.ByteCodeInstrumentor;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentClass;
import com.baidu.oped.apm.bootstrap.instrument.InstrumentException;
import com.baidu.oped.apm.bootstrap.interceptor.Interceptor;
import com.baidu.oped.apm.bootstrap.interceptor.tracevalue.DatabaseInfoTraceValue;
import com.baidu.oped.apm.profiler.modifier.AbstractModifier;
import com.baidu.oped.apm.profiler.modifier.db.interceptor.StatementExecuteQueryInterceptor;
import com.baidu.oped.apm.profiler.modifier.db.interceptor.StatementExecuteUpdateInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class MySQLStatementModifier 
 *
 * @author meidongxu@baidu.com
 */
public class MySQLStatementModifier extends AbstractModifier {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MySQLStatementModifier(ByteCodeInstrumentor byteCodeInstrumentor, Agent agent) {
        super(byteCodeInstrumentor, agent);
    }

    public String getTargetClass() {
        return "com/mysql/jdbc/StatementImpl";
    }

    public byte[] modify(ClassLoader classLoader, String javassistClassName, ProtectionDomain protectedDomain, byte[] classFileBuffer) {
        if (logger.isInfoEnabled()) {
            logger.info("Modifing. {}", javassistClassName);
        }

        try {
            InstrumentClass statementClass = byteCodeInstrumentor.getClass(classLoader, javassistClassName, classFileBuffer);

            Interceptor interceptor = new StatementExecuteQueryInterceptor();
            statementClass.addScopeInterceptor("executeQuery", new String[]{"java.lang.String"}, interceptor, MYSQLScope.SCOPE_NAME);

            // FIXME
            Interceptor executeUpdateInterceptor1 = new StatementExecuteUpdateInterceptor();
            statementClass.addScopeInterceptor("executeUpdate", new String[]{"java.lang.String"}, executeUpdateInterceptor1, MYSQLScope.SCOPE_NAME);

            Interceptor executeUpdateInterceptor2 = new StatementExecuteUpdateInterceptor();
            statementClass.addScopeInterceptor("executeUpdate", new String[]{"java.lang.String", "int"}, executeUpdateInterceptor2, MYSQLScope.SCOPE_NAME);

            Interceptor executeUpdateInterceptor3 = new StatementExecuteUpdateInterceptor();
            statementClass.addScopeInterceptor("execute", new String[]{"java.lang.String"}, executeUpdateInterceptor3, MYSQLScope.SCOPE_NAME);

            Interceptor executeUpdateInterceptor4 = new StatementExecuteUpdateInterceptor();
            statementClass.addScopeInterceptor("execute", new String[]{"java.lang.String", "int"}, executeUpdateInterceptor4, MYSQLScope.SCOPE_NAME);

            statementClass.addTraceValue(DatabaseInfoTraceValue.class);
            return statementClass.toBytecode();
        } catch (InstrumentException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("{} modify fail. Cause:{}", this.getClass().getSimpleName(), e.getMessage(), e);
            }
            return null;
        }
    }


}