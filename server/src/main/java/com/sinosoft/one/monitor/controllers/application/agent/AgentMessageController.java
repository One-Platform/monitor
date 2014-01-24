package com.sinosoft.one.monitor.controllers.application.agent;

import com.alibaba.fastjson.JSON;
import com.sinosoft.monitor.agent.store.model.Application;
import com.sinosoft.one.monitor.application.domain.ApplicationStore;
import com.sinosoft.one.mvc.web.annotation.Param;
import com.sinosoft.one.mvc.web.annotation.Path;
import com.sinosoft.one.mvc.web.annotation.rest.Get;
import com.sinosoft.one.mvc.web.annotation.rest.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 代理端消息处理Controller.
 * User: carvin
 * Date: 13-3-3
 * Time: 下午10:31
 */
@Path
public class AgentMessageController {
	private Logger logger = LoggerFactory.getLogger(AgentMessageController.class);

    @Autowired
    private ApplicationStore applicationStore;
    @Get("listApplication")
    @Post("listApplication")
    public String listApplication(@Param("ip") String ip,@Param("port") String port){
        List<Application> applicationList = applicationStore.getAgentApplicationStore(ip,port);
        String result = JSON.toJSONString(applicationList);
        return "@"+result;
    }
}
