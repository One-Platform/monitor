package com.sinosoft.one.monitor.os.linux.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.sinosoft.one.data.jade.parsers.util.StringUtil;
import com.sinosoft.one.monitor.os.linux.model.Os;
import com.sinosoft.one.monitor.os.linux.model.OsShell;

@DirtiesContext
@ContextConfiguration(locations = {"/spring/applicationContext.xml"})
public class OsServiceTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private OsService osService;
	
	
	@Test
	public void getOsBasic(){
//		List<Os> oss=osService.getOsBasicByIp("192.168.18.217"); 
//		for (Os os : oss) {
//			Assert.assertEquals("linux", os.getType());
//		}
		if(StringUtil.isEmpty(null)){
			System.out.println(111);
		}
		
	}
	
//	@Test
//	public void deleteOsBasic(){
//		osService.deleteOsBasic("402892163d1f4f23013d1f4f27220000");
//	}
//	
//	@Test
//	public void checkOsByIp(){
//		Assert.assertEquals(true, osService. checkOsByIp("192.168.18.222"));
//	}
	
	@Test
	public void saveShell(){
//		String template="top -b -n 1 | head -5 | tail -2 |awk '{print $1,$2,$3,$4,$5,$6,$7,$8,$9}'";
//		String CB="vmstat |tail -1|awk '{print $1,$2,$13,$14,$16,$15,$11}'";
//		String CU="/bin/sh \n"+
//				"CPULOG_1=$(cat /proc/stat | grep 'cpu ' | awk '{print $2\" \"$3\" \"$4\" \"$5\" \"$6\" \"$7\" \"$8}') \n" +
//				"SYS_IDLE_1=$(echo $CPULOG_1 | awk '{print $4}') \n" +
//				"Total_1=$(echo $CPULOG_1 | awk '{print $1+$2+$3+$4+$5+$6+$7}') \n" +
//				"CPULOG_2=$(cat /proc/stat | grep 'cpu ' | awk '{print $2\" \"$3\" \"$4\" \"$5\" \"$6\" \"$7\" \"$8}') \n" +
//				"SYS_IDLE_2=$(echo $CPULOG_2 | awk '{print $4}') \n" +
//				"Total_2=$(echo $CPULOG_2 | awk '{print $1+$2+$3+$4+$5+$6+$7}') \n" +
//				"SYS_IDLE=`expr $SYS_IDLE_2 - $SYS_IDLE_1` \n" +
//				"Total=`expr $Total_2 - $Total_1` \n" +
//				"SYS_USAGE=`expr $SYS_IDLE/$Total*100 |bc -l` \n" +
//				"SYS_Rate=`expr 100-$SYS_USAGE |bc -l` \n" +
//				"Disp_SYS_Rate=`expr \"scale=3; $SYS_Rate/1\" |bc` \n" +
//				"echo $Disp_SYS_Rate \n";
//		osService.saveShell("CU", CU);
		//磁盘脚本
//		String DK="df -k | awk '{print  $1,\"-\",$2,\"-\",$3,\"-\",$4,\"-\",$5,\"-\",$6,\",\"}'";
		String DK="df -k | awk '{print  $1,\":\",$2,\":\",$3,\":\",$4,\":\",$5,\":\",$6,\",\"}'";
		osService.saveShell("DK", DK);
		String RM="top -b -n 1 | head -5 | tail -2 |awk '{print $1,$2,$3,$4,$5,$6,$7,$8,$9}'";
		osService.saveShell("RM", RM);
		String CB="vmstat |tail -1|awk '{print $1,$2,$13,$14,$16,$15,$11}'";
		osService.saveShell("CB", CB);
	}
	
	@Test
	public void getShell(){
		 List<OsShell> osShells=osService.getOsShell();
		 for (OsShell osShell : osShells) {
			System.out.println(osShell.getTemplate());
		}
	}
	
}
