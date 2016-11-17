package cmcc.mobile.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ActivitiRoleGroupMapper;
import cmcc.mobile.admin.dao.ApprovalAuthorityMapper;
import cmcc.mobile.admin.dao.ApprovalBatchTaskMapper;
import cmcc.mobile.admin.dao.ApprovalDataMapper;
import cmcc.mobile.admin.dao.ApprovalRunManageMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigDetailsMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigMapper;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.dao.MsgCompanyMapper;
import cmcc.mobile.admin.dao.MsgSendMapper;
import cmcc.mobile.admin.dao.OtherMapper;
import cmcc.mobile.admin.dao.TemporaryBatchStartMapper;
import cmcc.mobile.admin.dao.ThirdApprovalDealMapper;
import cmcc.mobile.admin.dao.ThirdApprovalStartMapper;
import cmcc.mobile.admin.dao.UserApprovalDefDataMapper;
import cmcc.mobile.admin.dao.UserApprovalTypeMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ActivitiRoleGroup;
import cmcc.mobile.admin.entity.ApprovalAuthority;
import cmcc.mobile.admin.entity.ApprovalBatchTask;
import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalRunManage;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.MsgCompany;
import cmcc.mobile.admin.entity.MsgSend;
import cmcc.mobile.admin.entity.Other;
import cmcc.mobile.admin.entity.TemporaryBatchStart;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.ThirdApprovalStart;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.entity.UserApprovalDefData;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.service.iApprovalService;
import cmcc.mobile.admin.util.MessagePushInterface;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.vo.ProcessInfoVo;

@Service
public class ApprovalServiceImpl implements iApprovalService {

	@Autowired
	private ThirdApprovalDealMapper thirdApprovalDealMapper;

	@Autowired
	private ApprovalTypeMapper approvalTypeMapper;

	@Autowired
	private UserApprovalTypeMapper userApprovalTypeMapper;

	@Autowired
	private ApprovalTableConfigDetailsMapper approvalTableConfigDetailsMapper;

	@Autowired
	private ThirdApprovalStartMapper thirdApprovalStartMapper;

	@Autowired
	private ApprovalDataMapper approvalDataMapper;

	@Autowired
	private ApprovalTableConfigMapper approvalTableConfigMapper;

	@Autowired
	private ApprovalRunManageMapper approvalRunManageMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private OtherMapper otherMapper;

	@Autowired
	private CustomerMapper customerMapper;

	@Autowired
	private UserApprovalDefDataMapper userApprovalDefDataMapper;

	@Autowired
	private MsgSendMapper msgSendMapper;

	@Autowired
	private MsgCompanyMapper msgCompanyMapper;

	@Autowired
	private ApprovalAuthorityMapper approvalAuthorityMapper;
	@Autowired
	private ActivitiRoleGroupMapper groupMapper;

	@Autowired
	private ApprovalBatchTaskMapper approvalBatchTaskMapper;

	@Autowired
	private TemporaryBatchStartMapper temporaryBatchStartMapper;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 获取待办数量
	 */
	public List<ThirdApprovalDeal> getDealInfo(ThirdApprovalDeal record) {
		return thirdApprovalDealMapper.getDealInfo(record);
	}

	/**
	 * 获取置顶流程
	 */
	public List<ApprovalType> getTopProcess(Map<String, Object> map1) {
		return approvalTypeMapper.getTopProcess(map1);
	}

	/**
	 * 获取默认流程
	 */
	public List<ApprovalType> getDefaultProcess(Map<String, Object> map1) {
		return approvalTypeMapper.getDefaultProcess(map1);
	}

	/**
	 * 获取收藏流程
	 */
	public List<UserApprovalType> getCollectInfoByUserId(Map<String, Object> map1) {

		return userApprovalTypeMapper.getCollectInfoByUserId(map1);
	}

	/**
	 * 根据流程类型Id和公司Id获取流程类型数据
	 */

	public ApprovalType getApprovalTypeById(HashMap<String, String> map) {
		return approvalTypeMapper.getApprovalTypeById(map);
	}

	public ApprovalType getApprovalTypeById2(HashMap<String, String> map) {
		return approvalTypeMapper.getApprovalTypeById2(map);
	}

	/**
	 * 获取表单配置信息
	 */
	public List<ApprovalTableConfigDetails> getApprovalDetailsInfoById(String id) {
		List<ApprovalTableConfigDetails> controls = approvalTableConfigDetailsMapper.getApprovalInfoById(id);
		return controls;
	}

	public List<ApprovalTableConfigDetails> getApprovalDetailsInfoById(String id, String companyId, String mobile) {

		UserApprovalDefData param = new UserApprovalDefData();
		// id = "1466666355860363";
		param.setApprovalTableConfigId(id);
		param.setCompanyId(companyId);
		param.setMobile(mobile);
		List<UserApprovalDefData> aprovalData = userApprovalDefDataMapper.findByconfigIdAndUser(param);
		List<ApprovalTableConfigDetails> controls = approvalTableConfigDetailsMapper.getApprovalInfoById(id);

		for (UserApprovalDefData data : aprovalData) {
			String dcId = data.getControlId();
			for (ApprovalTableConfigDetails c : controls) {
				if (StringUtils.isNotEmpty(c.getReName()) && c.getReName().equals(dcId)) {
					if (c.getControlId().equals("DDMultiSelectField") || c.getControlId().equals("LinkageSelectField")
							|| c.getControlId().equals("DDSelectField")) {
						c.setJsonData(data.getJsonData());
						c.setValue("");
					} else if (c.getControlId().equals("DDPhotoField") || c.getControlId().equals("TextNote")) {
						c.setExp(data.getJsonData());
						c.setValue("");
					} else if (c.getControlId().equals("TableField")) {
						continue;
					} else {
						c.setValue(data.getJsonData());
					}
				}
			}
		}

		return controls;

	}

	public JsonResult addOther(Other other) {
		JsonResult json = new JsonResult();
		if (otherMapper.insert(other) > 0) {
			json.setSuccess(true);
		} else {
			json.setSuccess(false);
		}
		return json;
	}

	public User getUserById(String id) {
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 获取我的发起列表
	 */
	public List<ProcessInfoVo> getMeStartByUserId(HashMap<String, String> map) {
		return thirdApprovalStartMapper.getMeStartByUserId(map);
	}

	/**
	 * 获取待办列表
	 */
	public List<ProcessInfoVo> getApprovalListByUserId(ThirdApprovalDeal record) {
		return thirdApprovalDealMapper.getApprovalListByParams(record);
	}

	/**
	 * 根据流程ID获取流程信息
	 */
	public ApprovalData getApprovalDataByFlowId(String flowId) {

		return approvalDataMapper.selectByPrimaryKey(flowId);
	}

	/**
	 * 根据流程配置表ID获取流程配置表
	 */
	public ApprovalTableConfig getApprovalTableConfigById(String id) {

		return approvalTableConfigMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据流程ID获取流程扭转数据
	 * 
	 * @param flowId
	 * @return
	 */

	public List<ApprovalRunManage> getRunInfoByFlowId(String flowId) {
		return approvalRunManageMapper.getRunInfoByFlowId(flowId);
	}

	/**
	 * 开始流程
	 */
	public JsonResult startProcess(String approvalIds, String model, String typeId, String wyyId,
			String defaultApprovalIds, HttpServletRequest request, String thirdId, Long taskId, String isBatch) {
		JsonResult jsonresult = new JsonResult();

		if (!thirdId.equals("")) { // 批量发起
			ThirdApprovalDeal deal = thirdApprovalDealMapper.selectByPrimaryKey(thirdId);
			if (!deal.getNodeStatus().equals("1")) {
				jsonresult.setSuccess(false);
				jsonresult.setMessage("该待办已处理");
				return jsonresult;
			}
		}

		String userId = request.getSession().getAttribute("userId").toString();
		String path = request.getContextPath();
		String companyId = request.getSession().getAttribute("companyId").toString();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		UUID uuid = UUID.randomUUID();

		String run_id = "";
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("companyId", companyId);
		hashMap.put("id", typeId);
		ApprovalType approvalType = new ApprovalType();
		approvalType = approvalTypeMapper.getApprovalTypeById2(hashMap);
		String typename = approvalType.getName();
		String confId = approvalType.getApprovalTableConfigId();// 配置表单ID
		ApprovalTableConfig approvalTableConfig = new ApprovalTableConfig(); // 0待办
																				// //1待阅
		approvalTableConfig = approvalTableConfigMapper.selectByPrimaryKey(confId);
		String last_deal_way = approvalTableConfig.getLastDealWay(); // 获取默认审批人
																		// //
																		// 是代办还是代阅
		String last_user_id = approvalTableConfig.getLastUserId(); // 待阅

		// 插入approval_data
		String flowId = uuid.randomUUID().toString();
		String link = request.getScheme() + "://" + PropertiesUtil.getServerIp("server") + path + "/"
				+ "moblicApprove/toWorkflowDetail.do?flowid=" + flowId;
		// 批量发起的流程添加任务id
		if (taskId != null) {
			link += "&taskId=" + taskId;
		}
		logger.debug("ServerIp=============" + PropertiesUtil.getServerIp("server"));
		logger.debug("link地址地址地址地址：" + link);
		ApprovalData approvaldata = new ApprovalData();
		String draft_date = sdf.format(date);
		approvaldata.setFlowId(flowId);
		approvaldata.setDraftDate(draft_date);
		approvaldata.setJsonData(model);
		approvaldata.setNum(0);
		approvaldata.setStatus("1");
		approvaldata.setApprovalTableConfigId(confId);
		approvaldata.setUserId(userId);
		approvaldata.setCompanyId(companyId);
		if (null != approvalTableConfig.getDefaultApprovalUserIds()
				&& !"".equals(approvalTableConfig.getDefaultApprovalUserIds()))// 默认为空
			approvaldata.setIsDefinition("1");
		else
			approvaldata.setIsDefinition("0");
		approvalDataMapper.insertSelective(approvaldata);

		// 插入 approval_run_manage
		String runid = "";
		String arrive_date = sdf.format(date);
		String examine_data = "";
		String opinion = "";
		int run_no = 0;
		String status = "";
		for (int i = -1; i < approvalIds.split(",").length; i++) {
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			runid = uuid.randomUUID().toString();
			run_no = run_no + 100;
			if (i == -1) {
				examine_data = arrive_date;
				opinion = "发起申请";
				status = "2";
				approvalRunManage.setRunId(runid);
				approvalRunManage.setArriveDate(arrive_date);
				approvalRunManage.setExamineDate(examine_data);
				approvalRunManage.setOpinion(opinion);
				approvalRunManage.setRunNo(run_no);
				approvalRunManage.setRunStatus(status);
				approvalRunManage.setApprovalDataId(flowId);
				approvalRunManage.setUserId(userId);
				approvalRunManage.setCompanyId(companyId);
				approvalRunManageMapper.insertSelective(approvalRunManage);
			} else if (i == 0) {
				run_id = runid;
				approvalRunManage.setRunId(runid);
				approvalRunManage.setArriveDate(arrive_date);
				approvalRunManage.setRunNo(run_no);
				approvalRunManage.setRunStatus("1");
				approvalRunManage.setApprovalDataId(flowId);
				approvalRunManage.setUserId(approvalIds.split(",")[i]);
				approvalRunManage.setCompanyId(companyId);
				approvalRunManageMapper.insertSelective(approvalRunManage);
			} else {
				approvalRunManage.setRunId(runid);
				approvalRunManage.setRunNo(run_no);
				approvalRunManage.setRunStatus("0");
				approvalRunManage.setApprovalDataId(flowId);
				approvalRunManage.setUserId(approvalIds.split(",")[i]);
				approvalRunManage.setCompanyId(companyId);
				approvalRunManageMapper.insertSelective(approvalRunManage);
			}
		}
		if (last_user_id != null && !"".equals(last_user_id) && !("2").equals(last_deal_way)
				&& !"".equals(last_deal_way) && last_deal_way != null) {
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			runid = uuid.toString();
			run_no = run_no + 100;
			approvalRunManage.setRunId(runid);
			approvalRunManage.setRunNo(run_no);
			if ("0".equals(last_deal_way)) { // 待办
				approvalRunManage.setRunStatus("0");
			} else if ("1".equals(last_deal_way)) {// 待阅
				approvalRunManage.setRunStatus("5");
			}
			approvalRunManage.setApprovalDataId(flowId);
			approvalRunManage.setUserId(last_user_id);
			approvalRunManage.setCompanyId(companyId);
			approvalRunManageMapper.insertSelective(approvalRunManage);
		}
		User user = userMapper.selectByPrimaryKey(userId);
		String userName = user.getUserName();
		String approvalName = userName + "的" + typename + "申请";
		// 插入third_approval_start
		if (thirdId.equals("")) { // 不是批量发起
			insertThirdStart(request, "1", userId, link, approvalName, companyId, flowId, wyyId);
		} else {

		}
		if (thirdId.equals("")) { // 不是批量发起
			insertThirdDeal(request, userId, "1", approvalIds.split(",")[0], approvalName, link, companyId, flowId, "1",
					wyyId);
		} else {
			ThirdApprovalDeal deal = thirdApprovalDealMapper.selectByPrimaryKey(thirdId);
			if (deal.getNodeStatus().equals("1")) {
				insertThirdDeal(request, userId, "1", approvalIds.split(",")[0], approvalName, link, companyId, flowId,
						"1", wyyId);
				updateThirdDeal3(request, thirdId, "2", link, flowId);
			}
		}
		User user2 = userMapper.selectByPrimaryKey(approvalIds.split(",")[0]);
		String mobile = user2.getMobile();
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("typeName", approvalName);
		jsonresult.setModel(map);
		jsonresult.setSuccess(true);
		MessagePushInterface.messagePush(mobile, approvalName);

		// 如果是场景二的批量发起
		if ("1".equals(isBatch)) {
			TemporaryBatchStart start = new TemporaryBatchStart();
			start.setTaskId(taskId);
			start.setThirdId(thirdId);
			start.setFlowId(flowId);
			temporaryBatchStartMapper.updateByThirdId(start);

			ApprovalBatchTask task = approvalBatchTaskMapper.selectByPrimaryKey(taskId);
			if (task != null) {
				// 更新批量发起任务办理人数
				int num = task.getDonetaskUsers() != null ? task.getDonetaskUsers() : 0;
				num++;
				task.setDonetaskUsers(num);
				approvalBatchTaskMapper.updateByPrimaryKeySelective(task);
			}
		}

		return jsonresult;
	}

	/**
	 * 短消息接口
	 * 
	 * @param mobile
	 * @param typeName
	 * @return
	 */
	public JsonResult insertMsgSend(String mobile, String typeName) {
		JsonResult json = new JsonResult();
		MsgSend msgsend = new MsgSend();
		msgsend.setMobile(mobile);
		msgsend.setContent("V网通移动审批中您有一条新待办[" + typeName + "]，请您及时处理。");
		msgsend.setInserttime(new Date());
		if (msgSendMapper.insertSelective(msgsend) > 0) {
			json.setSuccess(true);
		} else {
			json.setSuccess(false);
			json.setMessage("服务器异常");
		}
		return json;
	}

	/**
	 * 已阅接口
	 */
	public JsonResult alRead(String flowId, String message, HttpServletRequest request) {
		JsonResult jsonresult = new JsonResult();
		Date date = new Date();
		String userId = request.getSession().getAttribute("userId").toString();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApprovalRunManage approvalRunManage = new ApprovalRunManage();
		approvalRunManage.setApprovalDataId(flowId);
		approvalRunManage.setUserId(userId);
		approvalRunManage.setRunStatus("5");
		approvalRunManage = approvalRunManageMapper.getRunInofByFlowId(approvalRunManage);
		if (approvalRunManage == null) {
			jsonresult.setSuccess(false);
			jsonresult.setMessage("数据不存在");
			return jsonresult;
		}
		approvalRunManage.setRunStatus("6"); // 已阅
		approvalRunManage.setExamineDate(sdf.format(date));
		approvalRunManage.setOpinion(message);
		approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);

		ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
		thirdApprovalDeal.setRunId(flowId);
		thirdApprovalDeal.setUserId(userId);
		thirdApprovalDeal.setNodeStatus("3");
		thirdApprovalDeal = thirdApprovalDealMapper.getApprovalByParams(thirdApprovalDeal).get(0);
		if (thirdApprovalDeal == null) {
			jsonresult.setSuccess(false);
			jsonresult.setMessage("数据不存在");
			return jsonresult;
		}
		thirdApprovalDeal.setExamineDate(sdf.format(date));
		thirdApprovalDeal.setNodeStatus("4");

		if (thirdApprovalDealMapper.updateByPrimaryKeySelective(thirdApprovalDeal) > 0)
			jsonresult.setSuccess(true);
		else {
			jsonresult.setSuccess(false);
			jsonresult.setMessage("服务器异常");
		}

		return jsonresult;
	}

	/**
	 * 办理流程
	 */
	public JsonResult ManageProcess(String flowid, String type, String message, HttpServletRequest request,
			String userid, String wyyId, String jsondata, String thirdDealId, String status, Long taskId) {

		String companyId = request.getSession().getAttribute("companyId").toString();
		String path = request.getContextPath();
		JsonResult jsonresult = new JsonResult();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random rd = new Random();
		String companyid = request.getSession().getAttribute("companyId").toString();
		String userId = request.getSession().getAttribute("userId").toString();
		Date date = new Date();
		String exameDate = sdf.format(date);

		if (!"cx".equals(type)) {
			ThirdApprovalDeal approvaldeal = new ThirdApprovalDeal();
			approvaldeal = thirdApprovalDealMapper.selectByPrimaryKey(thirdDealId);
			if (approvaldeal.getNodeStatus().equals("2")) {
				jsonresult.setMessage("该待办已经处理");
				jsonresult.setSuccess(false);
				return jsonresult;
			}
		}

		if ("cx".equals(type)) {
			// 更新approval_data
			ApprovalData approvalData = new ApprovalData();
			approvalData.setFlowId(flowid);
			approvalData = approvalDataMapper.selectByPrimaryKey(flowid);
			if ("2".equals(approvalData.getStatus()) || "3".equals(approvalData.getStatus()))
				approvalData.setJsonData(jsondata);
			approvalData.setStatus("9"); // 撤销
			approvalDataMapper.updateByPrimaryKeySelective(approvalData);

			// 更新Third_Approval_Start
			/**
			 * ThirdApprovalStart start = new ThirdApprovalStart();
			 * start.setRunId(flowid); start.setStatus("2");
			 * thirdApprovalStartMapper.updateByRunIdSelective(start);
			 */
			updateThirdStart(request, "2", flowid);

			// 更新Third_Approval_Deal
			ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
			thirdApprovalDeal.setRunId(flowid);
			thirdApprovalDeal.setNodeStatus("1");
			thirdApprovalDeal.setUserStartId(userId);
			thirdApprovalDeal.setWyyId(wyyId);
			thirdApprovalDeal = thirdApprovalDealMapper.getApprovalListByFlowId(thirdApprovalDeal);
			String thirdid = thirdApprovalDeal.getId();
			thirdApprovalDeal.setStatus("2");
			thirdApprovalDealMapper.updateByPrimaryKeySelective(thirdApprovalDeal);

			// updateThirdDeal(request, thirdDealId, "2");

			// 更新Approval_Run_Manage
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			approvalRunManage.setApprovalDataId(flowid);
			approvalRunManage.setRunStatus("1");
			approvalRunManage.setThirdId(thirdDealId);
			approvalRunManage = approvalRunManageMapper.getRunInofByFlowIdAndUserId(approvalRunManage);
			if (approvalRunManage != null) {
				approvalRunManage.setOpinion("起草人已撤回");
				approvalRunManage.setExamineDate(sdf.format(date));
				approvalRunManage.setRunStatus("4");
				int run_no = approvalRunManage.getRunNo();
				approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);
				approvalRunManage = new ApprovalRunManage();
				approvalRunManage.setApprovalDataId(flowid);
				approvalRunManage.setRunNo(run_no);
				approvalRunManage.setUserId(userId);
				List<ApprovalRunManage> runList = new ArrayList<ApprovalRunManage>();
				runList = approvalRunManageMapper.getRunInfoByRunNO(approvalRunManage);
				for (int i = 0; i < runList.size(); i++) {
					runList.get(i).setOpinion("起草人已撤回");
					runList.get(i).setRunStatus("4");
					runList.get(i).setExamineDate(sdf.format(date));
					approvalRunManageMapper.updateByPrimaryKeySelective(runList.get(i));
				}
			}
			jsonresult.setSuccess(true);
		} else if ("ty".equals(type)) {// 同意
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			approvalRunManage.setApprovalDataId(flowid);
			approvalRunManage.setRunStatus("1");
			approvalRunManage.setUserId(userId);
			int runno = approvalRunManageMapper.getRunNoByFlowId(approvalRunManage);
			approvalRunManage = approvalRunManageMapper.getRunInofByFlowIdAndUserId(approvalRunManage);
			List<ApprovalRunManage> runlist = new ArrayList<ApprovalRunManage>();
			runlist = approvalRunManageMapper.getAllInfo(flowid);

			if (runno == approvalRunManage.getRunNo()) { // 代办 最后一步
				// 更新data
				ApprovalData approvalData = new ApprovalData();
				approvalData = approvalDataMapper.selectByPrimaryKey(flowid);
				if ("2".equals(approvalData.getStatus()) || "3".equals(approvalData.getStatus()))
					approvalData.setJsonData(jsondata);
				approvalData.setStatus("6");
				approvalDataMapper.updateByPrimaryKeySelective(approvalData);
				// 更新start
				/**
				 * ThirdApprovalStart thirdApprovalStart = new
				 * ThirdApprovalStart(); thirdApprovalStart.setRunId(flowid);
				 * thirdApprovalStart.setStatus("2");
				 */
				updateThirdStart(request, "2", flowid);
				// thirdApprovalStartMapper.updateByRunIdSelective(thirdApprovalStart);
				// 更新run_manage
				approvalRunManage.setRunStatus("2");
				approvalRunManage.setUserId(userId);
				approvalRunManage.setExamineDate(sdf.format(date));
				approvalRunManage.setOpinion(message);
				approvalRunManage.setWyyId(wyyId);
				approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);

				// 更新deal
				ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
				List<ThirdApprovalDeal> dealList = new ArrayList<ThirdApprovalDeal>();
				dealList = thirdApprovalDealMapper.getAllDealInfoByFlowId(flowid);
				thirdApprovalDeal = dealList.get(0);
				for (int i = 0; i < dealList.size(); i++) {
					ThirdApprovalDeal deal = new ThirdApprovalDeal();
					if (i == dealList.size() - 1) {
						deal = dealList.get(dealList.size() - 1);
						deal.setExamineDate(sdf.format(date));
						deal.setStatus("2");
						deal.setNodeStatus("2");
						thirdApprovalDealMapper.updateByPrimaryKeySelective(deal);
					}
					deal = dealList.get(i);
					deal.setStatus("2");
					thirdApprovalDealMapper.updateByPrimaryKeySelective(deal);
				}

				// 处理待阅
				ApprovalRunManage approvalRun = new ApprovalRunManage();
				approvalRun.setRunStatus("5");
				approvalRun.setApprovalDataId(flowid);
				approvalRun = approvalRunManageMapper.getRunInofByFlowIdAndUserId(approvalRun);
				if (approvalRun != null) {
					Date date2 = new Date();
					String third_deal_id = companyid + sdf2.format(new Date()) + rd.nextInt(1000);
					approvalRun.setArriveDate(sdf.format(date2));
					approvalRun.setThirdId(third_deal_id);
					approvalRunManageMapper.updateByPrimaryKeySelective(approvalRun);
					thirdApprovalDeal.setId(third_deal_id);
					thirdApprovalDeal.setStatus("2");
					thirdApprovalDeal.setArriveDate(sdf.format(date2));
					thirdApprovalDeal.setNextNodeId(approvalRun.getRunId());
					thirdApprovalDeal.setUserId(approvalRun.getUserId());
					thirdApprovalDeal.setNodeStatus("3");
					thirdApprovalDeal.setWyyId(wyyId);
					thirdApprovalDealMapper.insertSelective(thirdApprovalDeal);
				}
				// 更新任务
				updateTaskComUserNum(taskId);

			} else { // 同意 还有下一步
				ApprovalData approvalData = new ApprovalData();
				approvalData = approvalDataMapper.selectByPrimaryKey(flowid);
				if ("2".equals(approvalData.getStatus()) || "3".equals(approvalData.getStatus())) {
					approvalData.setJsonData(jsondata);
					approvalData.setStatus("3");
					approvalDataMapper.updateByPrimaryKeySelective(approvalData);
				}

				approvalRunManage.setRunStatus("2");
				approvalRunManage.setExamineDate(sdf.format(date));
				approvalRunManage.setOpinion(message);
				approvalRunManage.setWyyId(wyyId);
				approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);

				// String thirdid = approvalRunManage.getThirdId();
				ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
				ApprovalRunManage nextApprovalRunManage = new ApprovalRunManage();
				for (int i = 1; i < runlist.size(); i++) {
					if (runlist.get(i).getRunNo() > approvalRunManage.getRunNo()) {
						nextApprovalRunManage = runlist.get(i);
						break;
					}
				}

				String approvalId = nextApprovalRunManage.getUserId();
				nextApprovalRunManage.setArriveDate(sdf.format(date));
				nextApprovalRunManage.setRunStatus("1");
				nextApprovalRunManage.setWyyId(wyyId);
				approvalRunManageMapper.updateByPrimaryKeySelective(nextApprovalRunManage);

				thirdApprovalDeal = thirdApprovalDealMapper.selectByPrimaryKey(thirdDealId);
				String typeName = thirdApprovalDeal.getApprovalName();
				thirdApprovalDeal.setExamineDate(sdf.format(date));
				thirdApprovalDeal.setNodeStatus("2");
				thirdApprovalDeal.setWyyId(wyyId);
				thirdApprovalDealMapper.updateByPrimaryKeySelective(thirdApprovalDeal);

				// updateThirdDeal(request, thirdDealId, "2");

				thirdApprovalDeal.setId(UUID.randomUUID().toString());
				/**
				 * thirdApprovalDeal.setArriveDate(sdf.format(date));
				 * thirdApprovalDeal.setExamineDate(null);
				 * thirdApprovalDeal.setUserId(nextApprovalRunManage.getUserId()
				 * ); thirdApprovalDeal.setNodeStatus("1");
				 * thirdApprovalDealMapper.insertSelective(thirdApprovalDeal);
				 */
				String link = request.getScheme() + "://" + PropertiesUtil.getServerIp("server") + path + "/"
						+ "moblicApprove/toWorkflowDetail.do?flowid=" + thirdApprovalDeal.getRunId();
				if (taskId != null) {
					link += "&taskId=" + taskId;
				}
				insertThirdDeal(request, thirdApprovalDeal.getUserStartId(), "1", nextApprovalRunManage.getUserId(),
						thirdApprovalDeal.getApprovalName(), link, thirdApprovalDeal.getCompanyId(),
						thirdApprovalDeal.getRunId(), "1", wyyId);

				User user = userMapper.selectByPrimaryKey(approvalId);
				String mobile = user.getMobile();
				Map<String, String> map = new HashMap<String, String>();
				map.put("mobile", mobile);
				map.put("typeName", typeName);
				MessagePushInterface.messagePush(mobile, typeName);

				jsonresult.setModel(map);
			}
			jsonresult.setSuccess(true);
		} else if ("jj".equals(type)) {// 拒绝
			// 更新 approval_data
			ApprovalData data = new ApprovalData();
			data.setFlowId(flowid);
			data = approvalDataMapper.selectByPrimaryKey(flowid);
			if ("2".equals(data.getStatus()) || "3".equals(data.getStatus()))
				data.setJsonData(jsondata);
			data.setStatus("8");
			// data.setUserId(userId);
			approvalDataMapper.updateByPrimaryKeySelective(data);

			// 更新ThirdApprovalStart
			// ThirdApprovalStart thirdApprovalStart = new ThirdApprovalStart();
			// thirdApprovalStart.setRunId(flowid);
			// thirdApprovalStart.setStatus("2");
			// thirdApprovalStart.setUserId(userId);
			// thirdApprovalStartMapper.updateByRunIdSelective(thirdApprovalStart);

			updateThirdStart(request, "2", flowid);
			// 更新ApprovalRunManage
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			approvalRunManage.setApprovalDataId(flowid);
			approvalRunManage.setRunStatus("1");
			approvalRunManage.setUserId(userId);
			approvalRunManage = approvalRunManageMapper.getRunInofByFlowIdAndUserId(approvalRunManage);
			approvalRunManage.setExamineDate(sdf.format(date));
			approvalRunManage.setOpinion(message + "(拒绝)");
			approvalRunManage.setRunStatus("3");
			approvalRunManage.setWyyId(wyyId);
			approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);

			// 更新ThirdApprovalDeal
			/**
			 * ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
			 * thirdApprovalDeal.setRunId(flowid);
			 * thirdApprovalDeal.setUserId(userId);
			 * thirdApprovalDeal.setNodeStatus("1");
			 * 
			 * thirdApprovalDeal =
			 * thirdApprovalDealMapper.getApprovalListByFlowId(thirdApprovalDeal
			 * );
			 */
			updateThirdDeal2(request, thirdDealId, "2", "2");

			ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
			List<ThirdApprovalDeal> list = new ArrayList<ThirdApprovalDeal>();
			list = thirdApprovalDealMapper.getAllDealInfoByFlowId(flowid);
			for (int i = 0; i < list.size() - 1; i++) {
				thirdApprovalDeal = list.get(i);
				thirdApprovalDeal.setStatus("2");
				thirdApprovalDeal.setWyyId(wyyId);
				thirdApprovalDealMapper.updateByPrimaryKeySelective(thirdApprovalDeal);
			}
			jsonresult.setSuccess(true);

			// 更新任务
			updateTaskComUserNum(taskId);
		} else if ("zf".equals(type)) { // 转发
			// 更新data
			ApprovalData data = new ApprovalData();
			data = approvalDataMapper.selectByPrimaryKey(flowid);
			if ("2".equals(data.getStatus()) || "3".equals(data.getStatus())) {
				data.setJsonData(jsondata);
				data.setStatus("3");

				approvalDataMapper.updateByPrimaryKeySelective(data);
			}

			// 更新runManager
			String thirdId = companyid + sdf2.format(date);
			ApprovalData approvalData = new ApprovalData();
			approvalData = approvalDataMapper.selectByPrimaryKey(flowid);
			String config_id = approvalData.getApprovalTableConfigId();
			ApprovalRunManage approvalRunManage = new ApprovalRunManage();
			approvalRunManage.setApprovalDataId(flowid);
			approvalRunManage.setRunStatus("1");
			approvalRunManage.setWyyId(wyyId);
			// approvalRunManage.setUserId(userId);
			approvalRunManage = approvalRunManageMapper.getRunInofByFlowIdAndUserId(approvalRunManage);
			int run_no = approvalRunManage.getRunNo();
			approvalRunManage.setExamineDate(sdf.format(date));
			approvalRunManage.setOpinion(message + "已转发");
			approvalRunManage.setRunStatus("2");
			approvalRunManage.setWyyId(wyyId);
			approvalRunManageMapper.updateByPrimaryKeySelective(approvalRunManage);
			approvalRunManage = new ApprovalRunManage();
			String run_id = config_id + sdf2.format(date);
			approvalRunManage.setRunId(run_id);
			approvalRunManage.setArriveDate(sdf.format(date));
			approvalRunManage.setRunNo(run_no + 1);
			approvalRunManage.setRunStatus("1");
			approvalRunManage.setApprovalDataId(flowid);
			approvalRunManage.setUserId(userid);
			approvalRunManage.setThirdId(thirdId);
			approvalRunManage.setWyyId(wyyId);
			approvalRunManageMapper.insertSelective(approvalRunManage);

			/**
			 * 更新deal thirdApprovalDeal.setRunId(flowid);
			 * thirdApprovalDeal.setNodeStatus("1"); thirdApprovalDeal =
			 * thirdApprovalDealMapper.getApprovalListByFlowId(thirdApprovalDeal
			 * ); String approval_name = thirdApprovalDeal.getApprovalName();
			 * String startUserId = thirdApprovalDeal.getUserStartId(); String
			 * link = thirdApprovalDeal.getLink();
			 * thirdApprovalDeal.setNodeStatus("2");
			 * thirdApprovalDealMapper.updateByPrimaryKeySelective(
			 * thirdApprovalDeal);
			 */

			ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
			thirdApprovalDeal.setRunId(flowid);
			thirdApprovalDeal.setNodeStatus("1");
			thirdApprovalDeal.setWyyId(wyyId);
			thirdApprovalDeal = thirdApprovalDealMapper.getApprovalListByFlowId(thirdApprovalDeal);
			String approval_name = thirdApprovalDeal.getApprovalName();
			String startUserId = thirdApprovalDeal.getUserStartId();
			// 更新 thirdapprovaldeal
			updateThirdDeal(request, thirdDealId, "2", wyyId);

			User user = userMapper.selectByPrimaryKey(startUserId);

			String link = request.getScheme() + "://" + PropertiesUtil.getServerIp("server") + path + "/"
					+ "moblicApprove/toWorkflowDetail.do?flowid=" + flowid;
			if (taskId != null) {
				link += "&taskId=" + taskId;
			}
			insertThirdDeal(request, startUserId, "1", userid, approval_name, link, companyId, flowid, "1", wyyId);

			/**
			 * ThirdApprovalDeal thirdApprovalDeal = new ThirdApprovalDeal();
			 * thirdApprovalDeal.setRunId(flowid);
			 * thirdApprovalDeal.setNodeStatus("1"); thirdApprovalDeal =
			 * thirdApprovalDealMapper.getApprovalListByFlowId(thirdApprovalDeal
			 * ); String approval_name = thirdApprovalDeal.getApprovalName();
			 * String startUserId = thirdApprovalDeal.getUserStartId();
			 * thirdApprovalDeal = new ThirdApprovalDeal();
			 * thirdApprovalDeal.setId(thirdId);
			 * thirdApprovalDeal.setApprovalName(approval_name);
			 * thirdApprovalDeal.setArriveDate(sdf.format(date));
			 * thirdApprovalDeal.setLink(link);
			 * thirdApprovalDeal.setNextNodeId(run_id);
			 * thirdApprovalDeal.setRunId(flowid);
			 * thirdApprovalDeal.setStatus("1");
			 * thirdApprovalDeal.setApprovalTableConfigId(config_id);
			 * thirdApprovalDeal.setUserId(userid);
			 * thirdApprovalDeal.setNodeStatus("1");
			 * thirdApprovalDeal.setUserStartId(startUserId);
			 * thirdApprovalDealMapper.insertSelective(thirdApprovalDeal);
			 */

			User user2 = userMapper.selectByPrimaryKey(userid);
			String mobile = user2.getMobile();
			Map<String, String> map = new HashMap<String, String>();
			map.put("mobile", mobile);
			map.put("typeName", approval_name);
			MessagePushInterface.messagePush(mobile, approval_name);
			jsonresult.setModel(map);
			jsonresult.setSuccess(true);
		}

		return jsonresult;

	}

	public List<ApprovalRunManage> getAllInfo(String id) {

		return approvalRunManageMapper.getAllInfo(id);
	}

	public List<ApprovalData> getInfoByApprovalTableConfigId(HashMap<String, Object> map) {
		return approvalDataMapper.getInfoByApprovalTableConfigId(map);
	}

	public List<ApprovalTableConfig> getInfoByTypeId(String approvalTypeId) {
		return approvalTableConfigMapper.getInfoByTypeId(approvalTypeId);
	}

	public Customer getCustomerById(String id) {
		return customerMapper.selectByPrimaryKey(id);
	}

	public List<MsgCompany> getMsgCompany(String companyId) {
		return msgCompanyMapper.getMsgCompanyByCompanyId(companyId);
	}

	/**
	 * 更新 thirdDeal
	 * 
	 * @param thirdId
	 * @param status
	 * @return
	 */
	public boolean updateThirdDeal(HttpServletRequest request, String thirdId, String nodeStatus, String wyyId) {
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ThirdApprovalDeal deal = new ThirdApprovalDeal();
		deal.setId(thirdId);
		deal.setNodeStatus(nodeStatus);
		deal.setExamineDate(sdf.format(new Date()));
		deal.setWyyId(wyyId);
		if (thirdApprovalDealMapper.updateByPrimaryKeySelective(deal) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 更新 thirdDeal
	 * 
	 * @param thirdId
	 * @param status
	 * @return
	 */
	public boolean updateThirdDeal2(HttpServletRequest request, String thirdId, String status, String nodeStatus) {
		boolean flag = false;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ThirdApprovalDeal deal = new ThirdApprovalDeal();
		deal.setId(thirdId);
		deal.setStatus(status);
		deal.setExamineDate(sdf.format(new Date()));
		deal.setNodeStatus(nodeStatus);
		if (thirdApprovalDealMapper.updateByPrimaryKeySelective(deal) > 0) {
			flag = true;
		}
		return flag;
	}

	public boolean updateThirdDeal3(HttpServletRequest request, String thirdId, String nodeStatus, String link,
			String flowId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		boolean flag = false;
		ThirdApprovalDeal deal = new ThirdApprovalDeal();
		deal.setId(thirdId);
		deal.setNodeStatus(nodeStatus);
		deal.setLink(link + "&thirdId=" + thirdId + "&status=" + nodeStatus);
		deal.setRunId(flowId);
		deal.setExamineDate(sdf.format(new Date()));
		if (thirdApprovalDealMapper.updateByPrimaryKeySelective(deal) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 插入 thirdDeal
	 * 
	 * @param thirdId
	 * @param status
	 * @return
	 */
	public boolean insertThirdDeal(HttpServletRequest request, String startUserId, String status, String userId,
			String approvalName, String link, String companyId, String runId, String nodeStatus, String wyyId) {
		boolean flag = false;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		ThirdApprovalDeal deal = new ThirdApprovalDeal();
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		deal.setId(id);
		deal.setStatus(status);
		deal.setArriveDate(sdf.format(date));
		deal.setUserId(userId);
		deal.setApprovalName(approvalName);
		deal.setCompanyId(companyId);
		deal.setLink(link + "&thirdId=" + id + "&status=" + status);
		deal.setRunId(runId);
		deal.setNodeStatus(nodeStatus);
		deal.setUserStartId(startUserId);
		deal.setWyyId(wyyId);
		if (thirdApprovalDealMapper.insertSelective(deal) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 插入start
	 * 
	 * @param request
	 * @param status
	 * @param userId
	 * @param link
	 * @param approvalName
	 * @param companyId
	 * @return
	 */

	public boolean insertThirdStart(HttpServletRequest request, String status, String userId, String link,
			String approvalName, String companyId, String runId, String wyyId) {
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		ThirdApprovalStart start = new ThirdApprovalStart();
		start.setId(id);
		start.setStartDate(sdf.format(date));
		start.setUserId(userId);
		start.setLink(link + "&thirdId=" + id + "&status=" + status);
		start.setCompanyId(companyId);
		start.setApprovalName(approvalName);
		start.setRunId(runId);
		start.setStatus(status);
		start.setWyyId(wyyId);
		if (thirdApprovalStartMapper.insertSelective(start) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 更新 start
	 * 
	 * @param request
	 * @param status
	 * @param userId
	 * @param link
	 * @param approvalName
	 * @param companyId
	 * @return
	 */
	public boolean updateThirdStart(HttpServletRequest request, String status, String id) {
		boolean flag = false;

		ThirdApprovalStart start = new ThirdApprovalStart();
		start.setStatus(status);
		start.setRunId(id);

		if (thirdApprovalStartMapper.updateByRunIdSelective(start) > 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public List<ProcessInfoVo> getApprovalListByUserId2(ThirdApprovalDeal record) {

		return thirdApprovalDealMapper.getApprovalListByParams2(record);
	}

	// 获取默认流程
	@Override
	public JsonResult getAntuarity(String companyId, String userId, String wyyId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		Map<String, Object> map2 = new HashMap<>();
		map.put("companyId", companyId);
		map.put("wyyId", wyyId);
		// 先获取该产品下的所有流程
		List<ApprovalAuthority> list = approvalAuthorityMapper.selectById(map);
		List<ApprovalType> topApprovalList = new ArrayList<ApprovalType>();
		if (list != null) {
			String[] Id = new String[list.size()];
			// 循环遍历所有流程list
			for (int i = 0; i < list.size(); i++) {
				String userIds = list.get(i).getUserids();
				String roleIds = list.get(i).getRoleId();
				if (userIds != null && userIds.equals("")) {
					userIds = null;
				}
				if (roleIds != null && roleIds.equals("")) {
					roleIds = null;
				}

				// 如果userIds和roleId是null的，则有全部流程的权限
				if (userIds == null && roleIds == null) {
					Id[i] = list.get(i).getId();
				} else if (userIds != null && roleIds == null) {
					String[] arr = list.get(i).getUserids().split(",");
					for (int y = 0; y < arr.length; y++) {
						if (userId.equals(arr[y])) {
							Id[i] = list.get(i).getId();
						}
					}
					// 如果userIds是null,roleId不是空的，由于roleId可能是多个需要截取
				} else if (userIds == null && roleIds != null) {
					String[] roleId = roleIds.split(",");
					map.clear();
					map.put("roleId", roleId);
					List<ActivitiRoleGroup> group = groupMapper.selectByUsers(map);
					for (int j = 0; j < group.size(); j++) {
						if (group.get(j).getUserId().equals(userId)) {
							Id[i] = list.get(i).getId();
							break;
						}
					}
					// 如果两者都不为空
				} else if (userIds != null && roleIds != null) {
					String[] arr = list.get(i).getUserids().split(",");
					for (int y = 0; y < arr.length; y++) {
						if (userId.equals(arr[y])) {
							Id[i] = list.get(i).getId();
						}
					}

					String[] roleId = roleIds.split(",");
					map.clear();
					map.put("roleId", roleId);
					List<ActivitiRoleGroup> group = groupMapper.selectByUsers(map);
					for (int j = 0; j < group.size(); j++) {
						if (group.get(j).getUserId().equals(userId)) {
							Id[i] = list.get(i).getId();
						}
					}
					// 如果roleId是null
				} else if (userIds != null && roleIds == null) {
					String[] arr = list.get(i).getUserids().split(",");
					for (int y = 0; y < arr.length; y++) {
						if (userId.equals(arr[y])) {
							Id[i] = list.get(i).getId();
						}
					}
				}
			}
			map.clear();
			if (Id.length == 0) {
				map.put("top", topApprovalList);
				List<ApprovalType> defaultApprovalList = new ArrayList<ApprovalType>();
				map.put("top", topApprovalList);
				map.put("default", defaultApprovalList);
			} else {
				map.put("id", Id);
				List<ApprovalType> defaultApprovalList = approvalTypeMapper.getProcess(map);
				map.clear();
				map.put("top", topApprovalList);
				map.put("default", defaultApprovalList);
			}
			map2.put("companyId", companyId);
			map2.put("wyyId", wyyId);
			// 获取默认审批人
			List<ApprovalTableConfig> config = approvalTableConfigMapper.selectByTypeId(map2);
			String[] ar = new String[config.size()];
			for (int i = 0; i < config.size(); i++) {
				String uses = config.get(i).getDefaultApprovalUserIds();
				if (uses == null) {
					map.put("key", "0");
				} else {
					String[] arr = config.get(i).getDefaultApprovalUserIds().split(",");
					for (int y = 0; y < arr.length; y++) {
						if (arr[y].equals(userId)) {
							ar[i] = config.get(i).getApprovalTypeId();
						}
					}
				}
			}

			// 获取有报表权限的人
			List<ApprovalAuthority> authorities = approvalAuthorityMapper.selectById(map2);
			String[] ids = new String[authorities.size()];
			for (int i = 0; i < authorities.size(); i++) {
				String reportUsers = authorities.get(i).getReportUserIds();
				if (reportUsers != null) {
					String[] user = authorities.get(i).getReportUserIds().split(",");
					for (int y = 0; y < user.length; y++) {
						if (user[y].equals(userId)) {
							ids[i] = authorities.get(i).getId();
						}
					}
				}
			}
			List<ApprovalType> AntulList = new ArrayList<>();
			List<ApprovalType> Antul = new ArrayList<>();
			if (ids.length != 0) {
				map1.put("id", ids);
				AntulList = approvalTypeMapper.getProcess(map1);
				//
				if (AntulList.size() != 0) {
					map.put("key", "1");
				}
			}

			if (ar.length != 0) {
				map1.put("id", ar);
				Antul = approvalTypeMapper.getProcess(map1);
				AntulList.addAll(Antul);
				if (AntulList.size() != 0) {
					map.put("key", "1");
				}
			}
			map.put("AntulList", AntulList);
		}
		return new JsonResult(true, "操作成功", map);
	}

	/**
	 * 更新任务完成人数数量
	 * 
	 * @param taskId
	 *            任務id
	 */
	private void updateTaskComUserNum(Long taskId) {
		if (taskId != null) {
			ApprovalBatchTask task = approvalBatchTaskMapper.selectByPrimaryKey(taskId);
			if (task != null) {
				// 如果是批量发起的任务更新完成数量
				int num = task.getCompletetaskUsers() != null ? task.getCompletetaskUsers() : 0;// 完成的人数
				num++;
				task.setCompletetaskUsers(num);
				approvalBatchTaskMapper.updateByPrimaryKeySelective(task);
			}
		}

	}

}
