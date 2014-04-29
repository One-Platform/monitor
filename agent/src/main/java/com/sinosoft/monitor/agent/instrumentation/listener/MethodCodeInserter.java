package com.sinosoft.monitor.agent.instrumentation.listener;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-3-20
 * Time: 下午12:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class MethodCodeInserter implements CodeInserter {

    private String owner;
    private String name;
    private int index;

    public MethodCodeInserter(String owner,String name,int index){
        this.owner = owner;
        this.name = name;
        this.index = index;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
