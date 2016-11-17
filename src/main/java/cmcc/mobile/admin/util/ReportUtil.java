package cmcc.mobile.admin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmcc.mobile.admin.entity.Organization;

public class ReportUtil {
	/**
	 * 日 周 月
	 * 获取查询开始时间和结束时间
	 * @param type
	 * @throws ParseException 
	 */
	public static List<String> getDate(String type) throws ParseException{
		String startdate="";
		String enddate = "";
		List<String> list = new ArrayList<String>();
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if("day".equals(type)){
			int w = cal.get(Calendar.DAY_OF_WEEK)-1;
			if(w==1){//查今天  周一
				startdate = sdf.format(new Date());
				enddate = sdf.format(new Date());
	        }else{ //查昨天
	        	cal.add(Calendar.DATE, -1);
	        	startdate = sdf.format(cal.getTime());
	        	enddate = sdf.format(cal.getTime());
	        }
		}else if("week".equals(type)){
			int w = cal.get(Calendar.DAY_OF_WEEK)-1;
	         if(w==0)
	    	     w=7;
	         cal.add(Calendar.DATE, -w+1);
	         Date end =cal.getTime(); 
	         startdate = sdf.format(end);
	         if(w==1){//周一
	        	 enddate = sdf.format(new Date());
	         }else{
	        	 cal.setTime(new Date());
	        	 cal.add(Calendar.DATE, -1);
	        	 enddate = sdf.format(cal.getTime());
	         }
		}else if("month".equals(type)){
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1;
			int w = cal.get(Calendar.DAY_OF_WEEK)-1;
			if(w==1){
				enddate = sdf.format(new Date());
			}else{
				cal.add(Calendar.DATE, -1);
				enddate = sdf.format(cal.getTime());
			}
			startdate = sdf.format(sdf.parse(year+"-"+month+"-"+"01"));
		}
		list.add(startdate);
		list.add(enddate);
		return list;
	}
	
	
	/**
	 * 获取父部门下所有子部门
	 * @param orgid
	 * @param list
	 * @param list2
	 * @return
	 */
	public static List<Organization> find(String orgid,List<Organization> list,List<Organization> list2){
		for(int i=0;i<list.size();i++){
			if(list.get(i).getPreviousId()!="" && list.get(i).getPreviousId()!=null){
				if(list.get(i).getPreviousId().equals(orgid)){
					Organization vo = new Organization();
					vo.setId(list.get(i).getId());
					vo.setOrgName(list.get(i).getOrgName());
					vo.setStatus(list.get(i).getStatus());
					list2.add(vo);
					find(list.get(i).getId(),list,list2);
				}
			}
		}
		return list2;
	}
	
	
	/**
	 * 排序
	 */
	
	public static Map<String,Object> sortMap(Map<String,Object> reportMap){
		
		List<Map.Entry<String, Object>> list_Data = new ArrayList<Map.Entry<String,Object>>(reportMap.entrySet());  
		 Collections.sort(list_Data,new Comparator<Map.Entry<String, Object>>() {  	 
	           public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {  
	        	   return ((int)Double.parseDouble(o1.getValue().toString())) - (int)Double.parseDouble(o2.getValue().toString());   
	            }  
	        }); 
		 
		 Map<String,Object> newMap = new LinkedHashMap<String,Object>();  
	        for (int i = list_Data.size()-1; i >= 0; i--) {  
	            newMap.put(list_Data.get(i).getKey(), list_Data.get(i).getValue());  
	     } 
	        
	        return newMap;
	}
}
