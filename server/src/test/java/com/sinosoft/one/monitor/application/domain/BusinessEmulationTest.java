package com.sinosoft.one.monitor.application.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.sinosoft.one.util.test.SpringTxTestCase;

@DirtiesContext
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class BusinessEmulationTest extends SpringTxTestCase {

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void applicationEmulationTest() throws InterruptedException {
       Thread.sleep(1000*10);
    }

//    @Test
//    public void applicationEumUrlTest(){
//        Application application = applicationService.findApplication("4028927e3d35247b013d3525d8ee0000");
//        Assert.assertTrue(!application.getEnumUrls().isEmpty());
//    }



}
