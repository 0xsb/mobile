package cmcc.mobile.admin.controller.zhuzy;


import java.math.BigDecimal;
import java.text.ParseException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.HyTemporaryLeave;
import cmcc.mobile.admin.entity.HyTemporarybx;
import cmcc.mobile.admin.entity.HyTemporarytrip;

import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.server.db.MultipleDataSource;

import cmcc.mobile.admin.service.ReportService;

import cmcc.mobile.admin.util.ReportUtil;
import cmcc.mobile.admin.vo.JsonData;



/**
 * 统计报表
 * @author zhuzy
 *
 */
@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private ReportService reportService;
	
	

	/**
	 * 报表
	 * @return
	 */
	@RequestMapping("/toReport")
	public String toReport() {
		return "/microApp/report/report";
	}	
	
	/**
	 * 统计报表
	 * @param type 请假qj 出差cc 报销bx
	 * @param term 日day 周week 月month
	 * @param companyId 公司ID 
	 * @param detailstype 统计人数 number 天数 day 钱 money
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("reportdata")
	@ResponseBody
	public JsonResult reportdata(String type,String term,String detailstype,
			HttpServletRequest request) throws ParseException{
		JsonResult json = new JsonResult();
		try{
			MultipleDataSource.setDataSourceKey("");
			String companyId = request.getSession().getAttribute("companyId").toString();
			String dbName = reportService.getCustomerById(companyId).getDbname();
			MultipleDataSource.setDataSourceKey(dbName);
			
			//获取部门数据
			List<Organization> orgList = new ArrayList<Organization>();//获取所有部门
			List<String> oneOrg = new ArrayList<String>(); //获取一级部门
			List<Organization> twoOrg = new ArrayList<Organization>(); //获取二级部门
			orgList = reportService.getAllOrg(companyId);
			
			for(int i=0;i<orgList.size();i++){
				if(orgList.get(i).getPreviousId()==null || orgList.get(i).getPreviousId().equals("")){
					oneOrg.add(orgList.get(i).getId());
				}
			}
			
			twoOrg = reportService.getOrgByPreId(oneOrg);
			List<List<Organization>> allOrg = new ArrayList<List<Organization>>();
			for(int i=0;i<twoOrg.size();i++){
				List<Organization> orgList2 = new ArrayList<Organization>();
				orgList2.add(twoOrg.get(i));
				orgList2.addAll(ReportUtil.find(twoOrg.get(i).getId(),orgList,new ArrayList<Organization>()));
				allOrg.add(orgList2);
			} 
			
			//获取开始时间   和 结束时间		
			List<String> datelist = new ArrayList<String>();
			datelist = ReportUtil.getDate(term);
			String startdate = datelist.get(0);
			String enddate = datelist.get(1);
			
			/**
			ReportFactory reportFactory = new ReportFactory();
			reportInterface = reportFactory.getReport(type);
			json = reportInterface.report(companyId,allOrg,startdate,enddate);
			*/
			
			//统计出差报表数据
			if(type.equals("cc")){ //出差
				ApprovalType approvalType = new ApprovalType();
				HashMap<String,String> typemap = new HashMap<String,String>();
				typemap.put("name", "出差");
				typemap.put("companyId",companyId);
				approvalType = reportService.getApprovalTypeByName(typemap);
				
				if(approvalType==null){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				String typeId = approvalType.getId();
				List<ApprovalTableConfig> configList = new ArrayList<ApprovalTableConfig>();
				configList = reportService.getApprovalTableConfigByTypeId(typeId);
				
				if(configList==null || configList.size()==0){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				List<ApprovalData> approvalDataList = new ArrayList<ApprovalData>(); //所有流程数据
				
				for(int i=0;i<configList.size();i++){
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("configId",configList.get(i).getId());
					map.put("status","6");
					map.put("startDate",startdate);
					map.put("endDate",enddate);
					map.put("companyId", companyId);
					approvalDataList.addAll(reportService.getApprovalDataByConfId(map));
				}
				
				for(int i=0;i<approvalDataList.size();i++){
					ApprovalData data = approvalDataList.get(i);
					String userId = data.getUserId();
					String flowId = data.getFlowId();
					String configId = data.getApprovalTableConfigId();
					String jsonData = data.getJsonData();//json数据
					String orgId = reportService.getUserById(userId).getOrgId();
					String appledate = data.getDraftDate().substring(0, 10); //申请时间
					List<ApprovalTableConfigDetails> detailsList = new ArrayList<ApprovalTableConfigDetails>();
					detailsList = reportService.getDetailsByConfigId(configId);
					
					HashMap<String,String> map = new HashMap<String,String>();
					for(int j=0;j<detailsList.size();j++){
						map.put(detailsList.get(j).getDescribeName(), detailsList.get(j).getReName());
					}
					
					
					String startDate = map.get("开始时间");
					String endDate = map.get("结束时间");
					String place = map.get("出差地点");
					String xcmx = map.get("行程明细");
					
					Map<String,Object> jsonmap = JSONObject.parseObject(jsonData);
					String jsondata =  jsonmap.get("data").toString();
					List<JsonData> list = JSONArray.parseArray(jsondata, JsonData.class);
					List<JsonData> infoList = new ArrayList<JsonData>();
					HyTemporarytrip trip = new HyTemporarytrip();
					trip.setUserId(userId);
					trip.setOrgId(orgId);
					trip.setAppleDate(appledate);
					
					for(int j=0;j<list.size();j++){
						if(list.get(j).getId().equals(xcmx)){
							String s = list.get(j).getValue();
							List<Map<String,Object>> maplist = (List<Map<String,Object>>)JSON.parse(s);
							for(int k=0;k<maplist.size();k++){
								Map<String,Object> Infomap = new HashMap<String,Object>();
								Infomap = maplist.get(k);
								infoList = JSONArray.parseArray(Infomap.get("list").toString(), JsonData.class);
								for(int z=0;z<infoList.size();z++){
									if(infoList.get(z).getId().equals(startDate)){
										trip.setStartdate(infoList.get(z).getValue());
									}else if(infoList.get(z).getId().equals(endDate)){
										trip.setEnddate(infoList.get(z).getValue());
									}else if(infoList.get(z).getId().equals(place)){
										trip.setTriplocale(infoList.get(z).getValue());
									}
								}
								UUID uuid = UUID.randomUUID();
							    trip.setId(uuid.toString());
							    reportService.addTrip(trip);
							}
							break;
						}
					}  
				}
				
				HashMap<String,Object> tripmap = new HashMap<String,Object>();
				tripmap.put("startdate",startdate);
				tripmap.put("enddate",enddate);
				List<HyTemporarytrip> tripList = reportService.getTripNumByOrg(tripmap);
				int totalNumber = tripList.size();
				
				HashMap<String,Integer> orgMap = new HashMap<String,Integer>();
				for(int i=0;i<tripList.size();i++){
					if(orgMap.get(tripList.get(i).getOrgId())==null){
						orgMap.put(tripList.get(i).getOrgId(),1);
					}else{
						orgMap.put(tripList.get(i).getOrgId(),1+orgMap.get(tripList.get(i).getOrgId()));
					}
				}	
				
				HashMap<String,Object> reportMap = new HashMap<String,Object>();
				//reportMap.put("total", totalNumber);
				int number = 0;
				for(int i=0;i<allOrg.size();i++){
					List<Organization> orglist = allOrg.get(i);
					String orgName = orglist.get(0).getOrgName();
					for(int j=0;j<orglist.size();j++){
						if(orgMap.get(orglist.get(j).getId())!=null){
							number = orgMap.get(orglist.get(j).getId()) + number;
						}
					}
					reportMap.put(orgName, number);
					number = 0;
				}
				
				Map<String,Object> newMap = new LinkedHashMap<String,Object>();  
				newMap =  ReportUtil.sortMap(reportMap);
		        newMap.put("startdate", startdate);
		        newMap.put("enddate", enddate);
		        newMap.put("total",totalNumber);
			    json.setSuccess(true);
			    json.setModel(newMap);
			    reportService.deletaAllTrip();
			}else if("qj".equals(type)){//请假
				ApprovalType approvalType = new ApprovalType();
				HashMap<String,String> typemap = new HashMap<String,String>();
				typemap.put("name", "请假");
				typemap.put("companyId",companyId);
				approvalType = reportService.getApprovalTypeByName(typemap);
				
				if(approvalType==null){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				String typeId = approvalType.getId();
				List<ApprovalTableConfig> configList = new ArrayList<ApprovalTableConfig>();
				configList = reportService.getApprovalTableConfigByTypeId(typeId);
				
				if(configList==null || configList.size()==0){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				List<ApprovalData> approvalDataList = new ArrayList<ApprovalData>(); //所有流程数据
				
				for(int i=0;i<configList.size();i++){
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("configId",configList.get(i).getId());
					map.put("status","6");
					map.put("startDate",startdate);
					map.put("endDate",enddate);
					map.put("companyId", companyId);
					approvalDataList.addAll(reportService.getApprovalDataByConfId(map));
				}
				
				for(int i=0;i<approvalDataList.size();i++){
					ApprovalData data = approvalDataList.get(i);
					String userId = data.getUserId();
					String flowId = data.getFlowId();
					String configId = data.getApprovalTableConfigId();
					String jsonData = data.getJsonData();//json数据
					String orgId = reportService.getUserById(userId).getOrgId();
					String appledate = data.getDraftDate().substring(0, 10); //申请时间
					List<ApprovalTableConfigDetails> detailsList = new ArrayList<ApprovalTableConfigDetails>();
					detailsList = reportService.getDetailsByConfigId(configId);
					
					HashMap<String,String> map = new HashMap<String,String>();
					for(int j=0;j<detailsList.size();j++){
						map.put(detailsList.get(j).getDescribeName(), detailsList.get(j).getReName());
					}
					
					String startDate = map.get("开始时间");
					String endDate = map.get("结束时间");
					String day = map.get("请假天数(天)");
					String vacateType = map.get("请假类型");
					
					Map<String,Object> jsonmap = JSONObject.parseObject(jsonData);
					String jsondata =  jsonmap.get("data").toString();
					List<JsonData> list = JSONArray.parseArray(jsondata, JsonData.class);
					
					HyTemporaryLeave vacate = new HyTemporaryLeave();
					vacate.setUserId(userId);
					vacate.setOrgId(orgId);
					vacate.setLeavedate(appledate);
					
					for(int j=0;j<list.size();j++){
						if(startDate.equals(list.get(j).getId())){
							vacate.setStartdate(list.get(j).getValue());
						}else if(endDate.equals(list.get(j).getId())){
							vacate.setEnddata(endDate);
						}else if(day.equals(list.get(j).getId())){
							vacate.setLeaveday(new BigDecimal(list.get(j).getValue()));
						}else if(vacateType.equals(list.get(j).getId())){
							vacate.setLeavetype(list.get(j).getValue());
						}
					}
					UUID uuid = UUID.randomUUID();
					vacate.setId(uuid.toString());
				    reportService.addLeave(vacate);
				}  
				double total = 0;
				
				
				HashMap<String,Object> vacateMap = new HashMap<String,Object>();
				HashMap<String,Object> reportMap = new HashMap<String,Object>();
			    vacateMap.put("startdate",startdate);
			    vacateMap.put("enddate",enddate);
			    if(detailstype.equals("day")){//统计部门请假天数
			    	
			    	List<HyTemporaryLeave> daylist = reportService.getLeaveDayByOrg(vacateMap); //统计 部门 请假天数	
			    	HashMap<String,Double> orgMap = new HashMap<String,Double>();
			    	for(int i=0;i<daylist.size();i++){
			    		total =  total +daylist.get(i).getLeaveday().doubleValue();
			    		orgMap.put(daylist.get(i).getOrgId(), daylist.get(i).getLeaveday().doubleValue());
			    	}
			    	
					double day = 0;
					for(int i=0;i<allOrg.size();i++){
						List<Organization> orglist = allOrg.get(i);
						String orgName = orglist.get(0).getOrgName();
						for(int j=0;j<orglist.size();j++){
							if(orgMap.get(orglist.get(j).getId())!=null){
								day = orgMap.get(orglist.get(j).getId()).doubleValue() + day;
							}
						}
						reportMap.put(orgName, day);
						day = 0;
					}
			   }else if("number".equals(detailstype)){//统计部门请假人数
				   List<HyTemporaryLeave> numList = reportService.getLeaveNumberByOrg(vacateMap); 
				   total = numList.size();
				   HashMap<String,Integer> orgMap = new HashMap<String,Integer>();
					for(int i=0;i<numList.size();i++){
						if(orgMap.get(numList.get(i).getOrgId())==null){
							orgMap.put(numList.get(i).getOrgId(),1);
						}else{
							orgMap.put(numList.get(i).getOrgId(),1+orgMap.get(numList.get(i).getOrgId()));
						}
					}
					int number = 0;
					for(int i=0;i<allOrg.size();i++){
						List<Organization> orglist = allOrg.get(i);
						String orgName = orglist.get(0).getOrgName();
						for(int j=0;j<orglist.size();j++){
							if(orgMap.get(orglist.get(j).getId())!=null){
								number = orgMap.get(orglist.get(j).getId()) + number;
							}
						}
						reportMap.put(orgName, number);
						number = 0;
					}
				  
				   
			   }
				
			    Map<String,Object> newMap = new LinkedHashMap<String,Object>();  
				newMap =  ReportUtil.sortMap(reportMap);
		        newMap.put("startdate", startdate);
		        newMap.put("enddate", enddate);
		        if(detailstype.equals("number"))
		        	newMap.put("total",(int) total);
		        else 
		        	newMap.put("total",total);
			    json.setSuccess(true);
			    json.setModel(newMap); 	
				reportService.deleteAllLeave();
				
			}else if("bx".equals(type)){ //报销
				ApprovalType approvalType = new ApprovalType();
				HashMap<String,String> typemap = new HashMap<String,String>();
				typemap.put("name", "报销");
				typemap.put("companyId",companyId);
				approvalType = reportService.getApprovalTypeByName(typemap);
				
				if(approvalType==null){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				String typeId = approvalType.getId();
				List<ApprovalTableConfig> configList = new ArrayList<ApprovalTableConfig>();
				configList = reportService.getApprovalTableConfigByTypeId(typeId);
				
				if(configList==null || configList.size()==0){
					json.setSuccess(false);
					json.setMessage("不存在请假流程");
					return json;
				}
				
				List<ApprovalData> approvalDataList = new ArrayList<ApprovalData>(); //所有流程数据
				
				for(int i=0;i<configList.size();i++){
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("configId",configList.get(i).getId());
					map.put("status","6");
					map.put("startDate",startdate);
					map.put("endDate",enddate);
					map.put("companyId", companyId);
					approvalDataList.addAll(reportService.getApprovalDataByConfId(map));
				}
				
				for(int i=0;i<approvalDataList.size();i++){
					ApprovalData data = approvalDataList.get(i);
					String userId = data.getUserId();
					String flowId = data.getFlowId();
					String configId = data.getApprovalTableConfigId();
					String jsonData = data.getJsonData();//json数据
					String orgId = reportService.getUserById(userId).getOrgId();
					String appledate = data.getDraftDate().substring(0, 10); //申请时间
					List<ApprovalTableConfigDetails> detailsList = new ArrayList<ApprovalTableConfigDetails>();
					detailsList = reportService.getDetailsByConfigId(configId);
					
					HashMap<String,String> map = new HashMap<String,String>();
					for(int j=0;j<detailsList.size();j++){
						map.put(detailsList.get(j).getDescribeName(), detailsList.get(j).getReName());
					}
					
					String reimburseMoney = map.get("报销金额(元)");
					String reimburseInfo = map.get("报销明细");
					
					Map<String,Object> jsonmap = JSONObject.parseObject(jsonData);
					String jsondata =  jsonmap.get("data").toString();
					List<JsonData> list = JSONArray.parseArray(jsondata, JsonData.class);
					List<JsonData> infoList = new ArrayList<JsonData>();
					
					HyTemporarybx bx = new HyTemporarybx();
					bx.setBxDate(appledate);
					bx.setUserId(userId);
					bx.setOrgId(orgId);
					for(int j=0;j<list.size();j++){
						if(list.get(j).getId().equals(reimburseInfo)){
							String s = list.get(j).getValue();
							List<Map<String,Object>> maplist = (List<Map<String,Object>>)JSON.parse(s);
							for(int k=0;k<maplist.size();k++){
								Map<String,Object> Infomap = new HashMap<String,Object>();
								Infomap = maplist.get(k);
								infoList = JSONArray.parseArray(Infomap.get("list").toString(), JsonData.class);
								for(int z=0;z<infoList.size();z++){
									if(infoList.get(z).getId().equals(reimburseMoney)){
										bx.setBxMoney(new BigDecimal(infoList.get(z).getValue()));
									}
								}
								UUID uuid = UUID.randomUUID();
							    bx.setId(uuid.toString());
							    reportService.addBx(bx);
							}
							break;
						}
					} 
				}
					
					HashMap<String,Object> bxMap = new HashMap<String,Object>();
					HashMap<String,Object> reportMap = new HashMap<String,Object>();
					bxMap.put("startdate",startdate);
					bxMap.put("enddate",enddate);
					List<HyTemporarybx> moneyList = new ArrayList<HyTemporarybx>();
					
					
					double totalMoney = 0;
					int totalNumber = 0;
					
					
					
					if(detailstype.equals("money")){ //统计报销金额
						Map<String,Double> orgMap = new HashMap<String,Double>();
						moneyList = reportService.getBxMoneyByOrg(bxMap);
						for(int i=0;i<moneyList.size();i++){
							totalMoney =  totalMoney +moneyList.get(i).getBxMoney().doubleValue();
							orgMap.put(moneyList.get(i).getOrgId(),moneyList.get(i).getBxMoney().doubleValue());
				    	}
						
						double money = 0;
						for(int i=0;i<allOrg.size();i++){
							List<Organization> orglist = allOrg.get(i);
							String orgName = orglist.get(0).getOrgName();
							for(int j=0;j<orglist.size();j++){
								if(orgMap.get(orglist.get(j).getId())!=null){
									money = orgMap.get(orglist.get(j).getId()).doubleValue() + money;
								}
							}
							reportMap.put(orgName, money);
							money = 0;
						}
						
					}else if(detailstype.equals("number")){ //统计报销人数
						List<HyTemporarybx> numberList = new ArrayList<HyTemporarybx>();
						numberList = reportService.getBxNumberByOrg(bxMap);
						totalNumber = numberList.size();
						
						 HashMap<String,Integer> orgMap = new HashMap<String,Integer>();
							for(int i=0;i<numberList.size();i++){
								if(orgMap.get(numberList.get(i).getOrgId())==null){
									orgMap.put(numberList.get(i).getOrgId(),1);
								}else{
									orgMap.put(numberList.get(i).getOrgId(),1+orgMap.get(numberList.get(i).getOrgId()));
								}
							}
							int number = 0;
							for(int i=0;i<allOrg.size();i++){
								List<Organization> orglist = allOrg.get(i);
								String orgName = orglist.get(0).getOrgName();
								for(int j=0;j<orglist.size();j++){
									if(orgMap.get(orglist.get(j).getId())!=null){
										number = orgMap.get(orglist.get(j).getId()) + number;
									}
								}
								reportMap.put(orgName, number);
								number = 0;
							}
						
					}
					
				    Map<String,Object> newMap = new LinkedHashMap<String,Object>();  
					newMap =  ReportUtil.sortMap(reportMap);
			        newMap.put("startdate", startdate);
			        newMap.put("enddate", enddate);
			        if(detailstype.equals("number"))
			        	newMap.put("total", totalNumber);
			        else 
			        	newMap.put("total",totalMoney);
				    json.setSuccess(true);
				    json.setModel(newMap); 	
					reportService.deleteAllBx();
					
				}

		}catch(Exception e){
			logger.info(e);
			json.setSuccess(false);
			json.setMessage("服务器异常");
		}
		
	    return json;
	}
}
	