package com.sinosoft.monitor.agent.util;

/**
 * Created with IntelliJ IDEA.
 * User: 韩春亮
 * Date: 14-3-18
 * Time: 下午1:14
 * To change this template use File | Settings | File Templates.
 */
public class AgentKeyUtil {
    public static final String ONE_M_AGENT_KEY="_one_m_agent_key_";

    private static ThreadLocal<String> agentKeyThreadLocal = new ThreadLocal<String>();
    public synchronized static void setAgentKey(String agentKey){
        agentKeyThreadLocal.set(agentKey);
    }
    public synchronized static String getAgentKey(){
        return agentKeyThreadLocal.get();
    }
    public synchronized static void remove(){
        agentKeyThreadLocal.remove();
    }
    /**
     * 获取 ONE_M_AGENT_KEY 如果没有，则创建一个放入treadLocal并将其返回。
     * @return
     */
    public synchronized static String createOneMAgentKey(){
        String oneMAgentKey = agentKeyThreadLocal.get();
        if (oneMAgentKey!=null){
            return oneMAgentKey;
        }
        oneMAgentKey = createKey();
        agentKeyThreadLocal.set(oneMAgentKey);
        return oneMAgentKey;
    }
    private static String createKey(){
        return System.currentTimeMillis()+"";
    }
}
