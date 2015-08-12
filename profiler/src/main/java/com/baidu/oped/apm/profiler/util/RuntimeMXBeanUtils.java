
package com.baidu.oped.apm.profiler.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class RuntimeMXBeanUtils 
 *
 * @author meidongxu@baidu.com
 */
public final class RuntimeMXBeanUtils {
    private static final RuntimeMXBean RUNTIME_MBEAN = ManagementFactory.getRuntimeMXBean();

    private static long START_TIME = 0;
    private static int PID = 0;
    private static final Random RANDOM = new Random();

    private RuntimeMXBeanUtils() {
    }

    public static int getPid() {
        if (PID == 0) {
            PID = getPid0();
        }
        return PID;
    }
    
    public static List<String> getVmArgs() {
        final List<String> vmArgs = RUNTIME_MBEAN.getInputArguments();
        if (vmArgs == null) {
            return Collections.emptyList();
        }
        return vmArgs;
    }

    private static int getPid0() {
        final String name = RUNTIME_MBEAN.getName();
        final int pidIndex = name.indexOf('@');
        if (pidIndex == -1) {
            getLogger().log(Level.WARNING, "invalid pid name:" + name);
            return getNegativeRandomValue();
        }
        String strPid = name.substring(0, pidIndex);
        try {
            return Integer.parseInt(strPid);
        } catch (NumberFormatException e) {
            return getNegativeRandomValue();
        }
    }

    private static int getNegativeRandomValue() {
        final int abs = Math.abs(RANDOM.nextInt());
        if (abs == Integer.MIN_VALUE) {
            return -1;
        }
        return abs;
    }

    public static long getVmStartTime() {
        if (START_TIME == 0) {
            START_TIME = getVmStartTime0();
        }
        return START_TIME;
    }

    private static long getVmStartTime0() {
        try {
            return  RUNTIME_MBEAN.getStartTime();
        } catch (UnsupportedOperationException e) {
            final Logger logger = getLogger();
            logger.log(Level.WARNING, "RuntimeMXBean.getStartTime() unsupported. Caused:" + e.getMessage(), e);
            return System.currentTimeMillis();
        }
    }

    public static String getName() {
        return RUNTIME_MBEAN.getName();
    }

    private static Logger getLogger() {
        return Logger.getLogger(RuntimeMXBeanUtils.class.getName());
    }

}
