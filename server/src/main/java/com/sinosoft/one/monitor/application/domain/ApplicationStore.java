package com.sinosoft.one.monitor.application.domain;

import com.sinosoft.one.monitor.application.model.Application;
import com.sinosoft.one.monitor.application.model.Method;
import com.sinosoft.one.monitor.application.model.Url;
import com.sinosoft.one.monitor.application.repository.ApplicationRepository;
import com.sinosoft.one.monitor.application.repository.MethodRepository;
import com.sinosoft.one.monitor.application.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 创建缓存application数据
 * Created with IntelliJ IDEA.
 * User: daojian
 * Date: 13-10-16
 * Time: 下午8:00
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ApplicationStore {
    private List<Application> applications = new ArrayList<Application>();
    private Map<String,List<Url>> applicationUrlMap = new HashMap<String, List<Url>>();
    private Map<String,List<Method>> urlMethodMap = new HashMap<String, List<Method>>();
    private Map<String,List<com.sinosoft.monitor.agent.store.model.Application>> agentApplicationStore
            = new HashMap<String, List<com.sinosoft.monitor.agent.store.model.Application>>();
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private MethodRepository methodRepository;

    public  void load(){
        Map<String,List<Url>> applicationUrlMapTemp = new HashMap<String, List<Url>>();
        Map<String,List<Method>> urlMethodMapTemp = new HashMap<String, List<Method>>();
        Map<String,List<com.sinosoft.monitor.agent.store.model.Application>> agentApplicationStoreTemp
                = new HashMap<String, List<com.sinosoft.monitor.agent.store.model.Application>>();
        List<Application> applicationsTemp = applicationRepository.findAllActiveApplication();
        for (Application application:applicationsTemp){

            String applicationKey = application.getApplicationIp()+":"+application.getApplicationPort();
            List<com.sinosoft.monitor.agent.store.model.Application> agentApplicationList = agentApplicationStoreTemp.get(applicationKey);
            if (agentApplicationList==null){
                agentApplicationList = new ArrayList<com.sinosoft.monitor.agent.store.model.Application>();
                agentApplicationStoreTemp.put(applicationKey,agentApplicationList);
            }

            com.sinosoft.monitor.agent.store.model.Application agentApplication
                    = new com.sinosoft.monitor.agent.store.model.Application();
            agentApplication.setApplicationId(application.getId());
            agentApplication.setApplicationName(application.getApplicationName());
            agentApplication.setIp(application.getApplicationIp());
            agentApplication.setPort(application.getApplicationPort());
            List<com.sinosoft.monitor.agent.store.model.Url> agentUrls
                    = new ArrayList<com.sinosoft.monitor.agent.store.model.Url>();
            agentApplication.setSubUrls(agentUrls);
            agentApplicationList.add(agentApplication);

            String applicationId = application.getId();
            List<Url> urls =  urlRepository.findUrlByAppID(applicationId);
            applicationUrlMapTemp.put(applicationId,urls);
            for (Url url:urls){
                com.sinosoft.monitor.agent.store.model.Url agentUrl
                        = new com.sinosoft.monitor.agent.store.model.Url();
                agentUrl.setUrlId(url.getId());
                agentUrl.setUrl(url.getUrl());
                List<com.sinosoft.monitor.agent.store.model.Method> agentMethods
                        = new ArrayList<com.sinosoft.monitor.agent.store.model.Method>();
                agentUrl.setSubMethods(agentMethods);
                agentUrls.add(agentUrl);
                String urlId = url.getId();
                List<Method> methods = methodRepository.selectMethodsOfUrlById(urlId);
                for (Method method:methods){
                    com.sinosoft.monitor.agent.store.model.Method agentMethod
                            = new com.sinosoft.monitor.agent.store.model.Method();
                    agentMethod.setMethodId(method.getId());
                    agentMethod.setMethodClass(method.getClassName());
                    agentMethod.setMethod(method.getMethodName());
                    agentMethods.add(agentMethod);
                }
                urlMethodMapTemp.put(urlId,methods);
            }
        }
        applications = applicationsTemp;
        applicationUrlMap = applicationUrlMapTemp;
        urlMethodMap = urlMethodMapTemp;
        agentApplicationStore = agentApplicationStoreTemp;
    }
    /**
     * 返回值不允许为null
     * @return
     */
    public List<Application> getApplications() {
        return applications;
    }
    /**
     * 返回值不允许为null
     * @return
     */
    public List<Url> getUrls(String applicationId) {
        List<Url> urls = applicationUrlMap.get(applicationId);
        return urls==null? Collections.EMPTY_LIST:urls;
    }
    /**
     * 返回值不允许为null
     * @return
     */
    public List<Method> getMethods(String urlId) {
        List<Method> methods = urlMethodMap.get(urlId);
        return methods==null? Collections.EMPTY_LIST:methods;
    }

    public List<com.sinosoft.monitor.agent.store.model.Application> getAgentApplicationStore(String ip, String port){
        String key = ip+":"+port;
        return agentApplicationStore.get(key) == null?Collections.EMPTY_LIST:agentApplicationStore.get(key);
    }
}
