
package com.baidu.oped.apm.profiler;

import com.baidu.oped.apm.bootstrap.util.NetworkUtils;
import com.baidu.oped.apm.common.PinpointConstants;
import com.baidu.oped.apm.common.ServiceType;
import com.baidu.oped.apm.common.Version;
import com.baidu.oped.apm.common.util.BytesUtils;
import com.baidu.oped.apm.profiler.util.RuntimeMXBeanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class AgentInformationFactory 
 *
 * @author meidongxu@baidu.com
 */
public class AgentInformationFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AgentInformationFactory() {
    }

    public AgentInformation createAgentInformation(ServiceType serverType) {
        if (serverType == null) {
            throw new NullPointerException("serverType must not be null");
        }
        // For compatibility issue, use machineName as agentId when agengId is not provided.
        // This could be a problem if more than one server instances run on a box.
        final String machineName = NetworkUtils.getHostName();
        final String hostIp = NetworkUtils.getHostIp();
        final String agentId = getId("pinpoint.agentId", machineName, PinpointConstants.AGENT_NAME_MAX_LEN);
        final String applicationName = getId("pinpoint.applicationName", "UnknownApplicationName", PinpointConstants.APPLICATION_NAME_MAX_LEN);
        final long startTime = RuntimeMXBeanUtils.getVmStartTime();
        final int pid = RuntimeMXBeanUtils.getPid();
        return new AgentInformation(agentId, applicationName, startTime, pid, machineName, hostIp, serverType.getCode(), Version.VERSION);
    }

    private String getId(String key, String defaultValue, int maxlen) {
        String value = System.getProperty(key, defaultValue);
        validateId(value, key, maxlen);
        return value;
    }

    private void validateId(String id, String idName, int maxlen) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }

        // TODO AgengId should be validated before bootclass.
        // or agent should stop when validataion is failed here.
        final byte[] bytes = BytesUtils.toBytes(id);
        if (bytes.length > maxlen) {
            logger.warn("{} is too long(1~24). value={}", idName, id);
        }
    }
}
