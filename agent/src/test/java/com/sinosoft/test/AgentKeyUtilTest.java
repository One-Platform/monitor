package com.sinosoft.test;

import com.sinosoft.monitor.agent.util.AgentKeyUtil;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-3-18
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public class AgentKeyUtilTest {
    public static void main(String[] args) throws Exception{
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<10;i++){
                    try {
                        Thread.sleep(20);
                        System.out.println(AgentKeyUtil.createOneMAgentKey());
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
        Thread.sleep(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<10;i++){
                    try {
                        Thread.sleep(20);
                        System.out.println(AgentKeyUtil.createOneMAgentKey());
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
    }
}
