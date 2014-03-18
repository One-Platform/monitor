package com.sinosoft.monitor.agent.trackers;

import com.sinosoft.monitor.agent.util.AgentKeyUtil;

public class MethodTracker extends DefaultTracker {
	public MethodTracker(String className, String methodName, Object thiz, Object[] args) {
		super(className, methodName, thiz, args);
        //非rootTracker 获取ONE_M_KEY
        super.setOneMAgentKey(AgentKeyUtil.getAgentKey());
	}
}
