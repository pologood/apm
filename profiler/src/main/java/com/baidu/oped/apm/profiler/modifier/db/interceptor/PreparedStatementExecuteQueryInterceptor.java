
package com.baidu.oped.apm.profiler.modifier.db.interceptor;

import java.util.HashMap;
import java.util.Map;

import com.baidu.oped.apm.bootstrap.context.DatabaseInfo;
import com.baidu.oped.apm.bootstrap.context.Trace;
import com.baidu.oped.apm.bootstrap.context.TraceContext;
import com.baidu.oped.apm.bootstrap.interceptor.ByteCodeMethodDescriptorSupport;
import com.baidu.oped.apm.bootstrap.interceptor.MethodDescriptor;
import com.baidu.oped.apm.bootstrap.interceptor.SimpleAroundInterceptor;
import com.baidu.oped.apm.bootstrap.interceptor.TraceContextSupport;
import com.baidu.oped.apm.bootstrap.interceptor.tracevalue.BindValueTraceValue;
import com.baidu.oped.apm.bootstrap.interceptor.tracevalue.DatabaseInfoTraceValueUtils;
import com.baidu.oped.apm.bootstrap.interceptor.tracevalue.ParsingResultTraceValue;
import com.baidu.oped.apm.bootstrap.logging.PLogger;
import com.baidu.oped.apm.bootstrap.logging.PLoggerFactory;
import com.baidu.oped.apm.common.util.ParsingResult;

/**
 * class PreparedStatementExecuteQueryInterceptor 
 *
 * @author meidongxu@baidu.com
 */
public class PreparedStatementExecuteQueryInterceptor implements SimpleAroundInterceptor, ByteCodeMethodDescriptorSupport, TraceContextSupport {

    private static final int DEFAULT_BIND_VALUE_LENGTH = 1024;

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private MethodDescriptor descriptor;
    private TraceContext traceContext;
    private int maxSqlBindValueLength = DEFAULT_BIND_VALUE_LENGTH;

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }

        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        trace.traceBlockBegin();
        trace.markBeforeTime();
        try {
            DatabaseInfo databaseInfo = DatabaseInfoTraceValueUtils.__getTraceDatabaseInfo(target, UnKnownDatabaseInfo.INSTANCE);
            trace.recordServiceType(databaseInfo.getExecuteQueryType());

            trace.recordEndPoint(databaseInfo.getMultipleHost());
            trace.recordDestinationId(databaseInfo.getDatabaseId());

            ParsingResult parsingResult = null;
            if (target instanceof ParsingResultTraceValue) {
                parsingResult = ((ParsingResultTraceValue) target).__getTraceParsingResult();
            }
            Map<Integer, String> bindValue = null;
            if (target instanceof BindValueTraceValue) {
                bindValue = ((BindValueTraceValue)target).__getTraceBindValue();
            }
            if (bindValue != null) {
                String bindString = toBindVariable(bindValue);
                trace.recordSqlParsingResult(parsingResult, bindString);
            } else {
                trace.recordSqlParsingResult(parsingResult);
            }

            trace.recordApi(descriptor);
//            trace.recordApi(apiId);
            
            // Need to change where to invoke clean().
            // There is cleanParameters method but it's not necessary to intercept that method.
            // iBatis intentionally does not invoke it in most cases. 
            clean(target);


        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            }
        }

    }

    private void clean(Object target) {
        if (target instanceof BindValueTraceValue) {
            ((BindValueTraceValue) target).__setTraceBindValue(new HashMap<Integer, String>());
        }
    }

    private String toBindVariable(Map<Integer, String> bindValue) {
        final String[] temp = new String[bindValue.size()];
        for (Map.Entry<Integer, String> entry : bindValue.entrySet()) {
            Integer key = entry.getKey() - 1;
            if (temp.length < key) {
                continue;
            }
            temp[key] = entry.getValue();
        }

        return BindValueUtils.bindValueToString(temp, maxSqlBindValueLength);

    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        try {
            // TODO Test if it's success. if failed terminate. else calcaulte resultset fetch too. we'd better make resultset fetch optional.
            trace.recordException(throwable);
            trace.markAfterTime();
        } finally {
            trace.traceBlockEnd();
        }
    }

    @Override
    public void setMethodDescriptor(MethodDescriptor descriptor) {
        this.descriptor = descriptor;
        traceContext.cacheApi(descriptor);
    }


    @Override
    public void setTraceContext(TraceContext traceContext) {
        this.traceContext = traceContext;
        this.maxSqlBindValueLength = traceContext.getProfilerConfig().getJdbcMaxSqlBindValueSize();
    }
}
