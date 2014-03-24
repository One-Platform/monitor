package com.sinosoft.monitor.agent.instrumentation.listener;

import com.sinosoft.monitor.org.objectweb.asm.MethodVisitor;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-3-20
 * Time: 下午12:45
 * To change this template use File | Settings | File Templates.
 */
public interface CodeInserter {
    void insert(MethodVisitor mv);
}
