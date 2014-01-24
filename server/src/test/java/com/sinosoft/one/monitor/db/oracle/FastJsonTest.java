package com.sinosoft.one.monitor.db.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FastJsonTest {
	public static void main(String[] args) {
		//testFastJson1();
		System.out.println(JSON.toJSON(new JsonTestObj()));
	}
	public static void testFastJson(){
		JSONArray jsonArray = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		jsonObject.put("a1", "b1");
		jsonObject.put("a2", "b2");
		jsonObject1.put("s1", "s2");
		jsonObject1.put("arr", jsonArray2);
		jsonObject1.put("obj",jsonObject2);
		jsonArray.add(jsonObject);
		jsonArray.add(jsonObject1);
		System.out.println(jsonArray.toJSONString());
		System.out.println(jsonObject1.toJSONString());
	}
	public static void testFastJson1(){
		Map<String, String> mp = new HashMap<String, String>();
		mp.put("as", "sdd");
		
		System.out.println(JSON.toJSON(mp));
	}
	public static void testDoubleAndLong(){
		System.out.println(10000000000000000002.11);
		
	}
	public static class JsonTestObj {
		String id = "ass0011";
		String name = "assname";
//		String[] point = new String[]{"23","34","34"};
//		Map<String, String> mp = new HashMap<String, String>();
//		List<String> list = new ArrayList<String>();
	}
}
