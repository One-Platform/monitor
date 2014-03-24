package com.sinosoft.monitor.agent.instrumentation.listener;

import com.sinosoft.monitor.agent.JavaAgent;
import com.sinosoft.monitor.org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-3-20
 * Time: 上午11:04
 * 针对特定类的特定方法做监听
 *
 */
public class AsmVisitMethodListener {
    private MethodVisitor mv;
    //要监听的类
    private String className;
    //要监听的方法
    private String methodName;
    //命令-出现次数
    private Map<String,Integer> insnIndexMap = new HashMap<String, Integer>(0);
    private AsmVisitMethodListener(){}

    public static AsmVisitMethodListener newInstance(MethodVisitor mv,String className, String methodName) {
        AsmVisitMethodListener listener = new AsmVisitMethodListener();
        listener = new AsmVisitMethodListener();
        listener.mv = mv;
        listener.className = className;
        listener.methodName = methodName;
        return listener;
    }
    private Integer getIndex(int opcode,String owner, String name){
        String indexKey = opcode+"/"+owner+"/"+name;
        if (insnIndexMap.get(indexKey)==null){
            insnIndexMap.put(indexKey,0);
        }
        return insnIndexMap.get(indexKey);
    }
    private void increaseIndex(int opcode,String owner, String name){
        String indexKey = opcode+"/"+owner+"/"+name;
        int index = getIndex(opcode,owner,name);
        insnIndexMap.put(indexKey,++index);
    }

    /**
     * 代码插入，可以选择在目标事件触发前或者目标事件触发后插入
     * @param className  目标类
     * @param methodName 目标方法
     * @param opcode      目标指令
     * @param owner       调用方法的类名
     * @param name        调用方法的方法名
     * @param inserter   代码插入器
     */
    public void visitMethodInsn(String className, String methodName, int opcode,
                                String owner, String name, MethodCodeInserter inserter){
        //如果当前方法不是目标方法则取消监听
        if (!this.className.equals(className)||!this.methodName.equals(methodName))
            return;
        //如果当前命令不是目标命令则取消监听
        else if (!inserter.getOwner().equals(owner)||!inserter.getName().equals(name))
            return;
        else {
            int currIndex = getIndex(opcode,owner,name);
            int index = inserter.getIndex();
            JavaAgent.logger.log(Level.INFO,"AsmVisitMethodListener.visitMethodInsn log>>>>" +
                    "className:{0},methodName:{1},opcode:{2},owner:{3},name:{4},currIndex:{5},pointIndex:{6}",
                    new Object[]{className,methodName,opcode,owner,name,currIndex,index});
            if (currIndex==index){
                inserter.insert(mv);
                JavaAgent.logger.log(Level.INFO,"AsmVisitMethodListener.visitMethodInsn log<<< end");
            }
            else {
                increaseIndex(opcode,owner,name);
            }
        }
    }
}
