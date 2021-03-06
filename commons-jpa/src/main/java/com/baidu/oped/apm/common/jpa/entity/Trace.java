package com.baidu.oped.apm.common.jpa.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * The persistent class for the apm_span database table.
 */
@Entity
@Table(name = "apm_span", indexes = {
        @Index(name = "trace_instance_unique", columnList = "agent_id,span_id", unique = true)})
public class Trace extends AbstractPersistable<Long> implements ClearableAgentInfo {

    private static final long serialVersionUID = 7361021770775999516L;
    @Basic
    @Column(name = "agent_id", nullable = true, insertable = true, updatable = true)
    private Long agentId;

    @Column(name = "span_id", nullable = false, insertable = true, updatable = true)
    private long spanId;

    // api meta data unique id
    @Basic
    @Column(name = "api_id", nullable = true, insertable = true, updatable = true)
    private Long apiId;

    @Basic
    @Column(name = "collector_accept_time", nullable = true, insertable = true, updatable = true)
    private Long collectorAcceptTime;

    @Basic
    @Column(name = "elapsed", nullable = true, insertable = true, updatable = true)
    private int elapsed;

    @Column(name = "start_time", nullable = true, insertable = true, updatable = true)
    private long startTime;

    @Column(name = "end_point", length = 512)
    private String endPoint;

    @Column(name = "err_code")
    private int errCode;

    @Column(name = "exception_class", length = 512)
    private String exceptionClass;

    @Column(name = "exception_id")
    private int exceptionId;

    @Column(name = "exception_message", length = 512)
    private String exceptionMessage;

    private short flag;

    @Column(name = "has_exception", nullable = true)
    private boolean hasException = false;

    @Column(name = "parent_span_id")
    private long parentSpanId;

    @Column(name = "remote_addr", length = 512)
    private String remoteAddr;

    @Column(length = 512)
    private String rpc;

    @Column(name = "service_type")
    private int serviceType;

    @Column(name = "trace_agent_id", length = 128)
    private String traceAgentId;

    @Column(name = "trace_agent_start_time")
    private long traceAgentStartTime;

    @Column(name = "trace_transaction_sequence")
    private long traceTransactionSequence;

    @Basic
    @Column(name = "version", nullable = true, insertable = true, updatable = true)
    private int version;

    @Override
    public Long getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getCollectorAcceptTime() {
        return collectorAcceptTime;
    }

    public void setCollectorAcceptTime(Long collectorAcceptTime) {
        this.collectorAcceptTime = collectorAcceptTime;
    }

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public int getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(int exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public boolean isHasException() {
        return hasException;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }

    public long getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(long parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public long getSpanId() {
        return spanId;
    }

    public void setSpanId(long spanId) {
        this.spanId = spanId;
    }

    public String getTraceAgentId() {
        return traceAgentId;
    }

    public void setTraceAgentId(String traceAgentId) {
        this.traceAgentId = traceAgentId;
    }

    public long getTraceAgentStartTime() {
        return traceAgentStartTime;
    }

    public void setTraceAgentStartTime(long traceAgentStartTime) {
        this.traceAgentStartTime = traceAgentStartTime;
    }

    public long getTraceTransactionSequence() {
        return traceTransactionSequence;
    }

    public void setTraceTransactionSequence(long traceTransactionSequence) {
        this.traceTransactionSequence = traceTransactionSequence;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("agentId", agentId)
                .add("spanId", spanId)
                .add("apiId", apiId)
                .add("collectorAcceptTime", collectorAcceptTime)
                .add("elapsed", elapsed)
                .add("startTime", startTime)
                .add("endPoint", endPoint)
                .add("errCode", errCode)
                .add("exceptionClass", exceptionClass)
                .add("exceptionId", exceptionId)
                .add("exceptionMessage", exceptionMessage)
                .add("flag", flag)
                .add("hasException", hasException)
                .add("parentSpanId", parentSpanId)
                .add("remoteAddr", remoteAddr)
                .add("rpc", rpc)
                .add("serviceType", serviceType)
                .add("traceAgentId", traceAgentId)
                .add("traceAgentStartTime", traceAgentStartTime)
                .add("traceTransactionSequence", traceTransactionSequence)
                .add("version", version)
                .toString();
    }
}