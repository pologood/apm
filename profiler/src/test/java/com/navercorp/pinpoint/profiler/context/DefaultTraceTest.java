
package com.baidu.oped.apm.profiler.context;

import com.baidu.oped.apm.common.ServiceType;
import com.baidu.oped.apm.common.Version;
import com.baidu.oped.apm.profiler.AgentInformation;
import com.baidu.oped.apm.profiler.context.DefaultTrace;
import com.baidu.oped.apm.profiler.context.DefaultTraceContext;
import com.baidu.oped.apm.profiler.context.storage.SpanStorage;
import com.baidu.oped.apm.profiler.logging.Slf4jLoggerBinderInitializer;
import com.baidu.oped.apm.profiler.sender.LoggingDataSender;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class DefaultTraceTest 
 *
 * @author meidongxu@baidu.com
 */
public class DefaultTraceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeClass
    public static void before() throws Exception {
        Slf4jLoggerBinderInitializer.beforeClass();
    }

    @AfterClass
    public static void after()  throws Exception {
        Slf4jLoggerBinderInitializer.afterClass();
    }


    @Test
    public void testPushPop() {
        DefaultTraceContext defaultTraceContext = new DefaultTraceContext();
        defaultTraceContext.setAgentInformation(new AgentInformation("agentId", "applicationName", System.currentTimeMillis(), 10, "test", "127.0.0.1", ServiceType.TOMCAT.getCode(), Version.VERSION));
        DefaultTrace trace = new DefaultTrace(defaultTraceContext, 1);

        trace.setStorage(new SpanStorage(LoggingDataSender.DEFAULT_LOGGING_DATA_SENDER));

        Assert.assertEquals(0, trace.getCallStackDepth());

        trace.traceBlockBegin();

        Assert.assertEquals(1, trace.getCallStackDepth());

        trace.traceBlockBegin();
        Assert.assertEquals(2, trace.getCallStackDepth());

        trace.traceBlockEnd();

        Assert.assertEquals(1, trace.getCallStackDepth());

        trace.traceBlockEnd();

        Assert.assertEquals(0, trace.getCallStackDepth());

        trace.traceRootBlockEnd();

    }
}
