
package com.baidu.oped.apm.profiler.util;

import com.baidu.oped.apm.bootstrap.instrument.AttachmentFactory;
import com.baidu.oped.apm.bootstrap.instrument.AttachmentScope;
import com.baidu.oped.apm.bootstrap.instrument.Scope;

/**
 * class AttachmentThreadLocalScope 
 *
 * @author meidongxu@baidu.com
 */
public class AttachmentThreadLocalScope<T> implements AttachmentScope<T> {

    private final NamedThreadLocal<AttachmentScope<T>> scope;

    public AttachmentThreadLocalScope(final AttachmentSimpleScopeFactory<T> attachmentSimpleScopeFactory) {
        if (attachmentSimpleScopeFactory == null) {
            throw new NullPointerException("attachmentSimpleScopeFactory must not be null");
        }

        this.scope = new NamedThreadLocal<AttachmentScope<T>>(attachmentSimpleScopeFactory.getName()) {

            @Override
            @SuppressWarnings("unchecked")
            protected AttachmentScope<T> initialValue() {
                final Scope newScope = attachmentSimpleScopeFactory.createScope();
                if (newScope instanceof AttachmentScope) {
                    return (AttachmentScope<T>) newScope;
                }
                throw new IllegalArgumentException("invalid scope type");
            }
        };

    }

    @Override
    public int push() {
        final AttachmentScope<T> localScope = getLocalScope();
        return localScope.push();
    }

    @Override
    public int depth() {
        final AttachmentScope<T> localScope = getLocalScope();
        return localScope.depth();
    }

    @Override
    public int pop() {
        final AttachmentScope<T> localScope = getLocalScope();
        return localScope.pop();
    }

    protected AttachmentScope<T> getLocalScope() {
        return scope.get();
    }


    @Override
    public String getName() {
        return scope.getName();
    }

    @Override
    public T getOrCreate(AttachmentFactory<T> attachmentFactory) {
        final AttachmentScope<T> localScope = getLocalScope();
        return localScope.getOrCreate(attachmentFactory);
    }

    @Override
    public void setAttachment(T object) {
        final AttachmentScope<T> localScope = getLocalScope();
        localScope.setAttachment(object);
    }

    @Override
    public T getAttachment() {
        final AttachmentScope<T> localScope = getLocalScope();
        return localScope.getAttachment();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttachmentThreadLocalScope{");
        sb.append("scope=").append(scope.getName());
        sb.append('}');
        return sb.toString();
    }
}