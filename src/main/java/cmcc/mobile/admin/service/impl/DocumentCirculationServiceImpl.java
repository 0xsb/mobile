package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.ThirdApprovalStartMapper;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.ThirdApprovalStart;
import cmcc.mobile.admin.service.ActivitiRoleService;
import cmcc.mobile.admin.service.DocumentCirculationService;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.util.DateUtil;
import cmcc.mobile.admin.vo.ActivitiUserVo;

/**
 * 公文传阅实现类
 * 
 * @author renlinggao
 * @Date 2016年9月19日
 */
@Service
public class DocumentCirculationServiceImpl implements DocumentCirculationService {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private FormService formService;

	@Autowired
	private TaskService taskservice;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private ActivitiRoleService roleService;

	@Autowired
	private ProcessUtilService processUtilService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ThirdApprovalStartMapper startMapper;

	@Autowired
	private ApprovalTypeMapper typeMapper;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> agree(String userId, String taskId, Map<String, Object> params, String opinionTxt) {
		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		if (task == null)
			throw new RuntimeException("流程不存在");
		Map<String, Object> result = new HashMap<>();
		// 保存表单数据到流程里面
		taskservice.setVariablesLocal(task.getId(), params);
		boolean isSelectTask = false;// 是否需要选择下一步任务

		// String taskDefKey = processUtilService.getNextTask(taskId);
		Map nextActivity = processUtilService.getNextActivity(taskId);
		List<FlowNode> flowNodes = (List<FlowNode>) nextActivity.get("flowNodes");
		if ((boolean) nextActivity.get("needUserSelect")) {// 如果需要选择下一步的流转方向
			List<Map> nextNodes = new ArrayList<>();
			for (FlowNode f : flowNodes) {
				Map n = new HashMap<>();
				n.put("taskDefId", f.getId());
				n.put("taskName", f.getName());
				nextNodes.add(n);
			}
			result.put("nextNodes", nextNodes);// 把下一步的节点放入返回中
		} else {
			if (flowNodes != null && flowNodes.size() > 0) {
				String taskDefKey = flowNodes.get(0).getId();

				ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(task.getProcessDefinitionId()).singleResult();

				Map<String, Object> tasksConfig = (Map<String, Object>) getProcessConfig(processDefinition)
						.get("tasksConfig");
				// 获取下一个节点的节点配置信息
				Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(taskDefKey);
				// 获取当前节点的节点配置信息
				Map<String, Object> currentTaskConfig = (Map<String, Object>) tasksConfig
						.get(task.getTaskDefinitionKey());

				taskConfig.put("id", taskDefKey);
				List<Map<String, Object>> paramsList = new ArrayList<Map<String, Object>>();
				paramsList.add(taskConfig);
				List userList = null;
				if (taskConfig.containsKey("__DontSelectCurrentTaskUser")
						|| currentTaskConfig.containsKey("__DontSelectUser")) {
					userList = new ArrayList<ActivitiUserVo>();
					ActivitiUserVo user = new ActivitiUserVo();
					user.setUserId(userId);
					user.setUserName("已选择预设用户");
					userList.add(user);
				} else {
					try {
						Map<String, Object> taskUsers = (Map<String, Object>) roleService
								.getNextNode(userId, null, true, paramsList).getModel();
						userList = (List) taskUsers.get("users");
						if (userList == null || userList.size() == 0)
							throw new RuntimeException();
					} catch (Exception e) {
						throw new RuntimeException("角色配置错误");
					}
				}

				// 当前节点类型 0普通节点，1 多人节点
				String userTaskType = Objects.toString(taskConfig.get("__userTaskType"), "0");
				result.put("userTaskType", userTaskType);
				result.put("taskId", taskId);
				result.put("users", userList);
			} else {
				result.put("taskId", taskId);
				result.put("isEnd", true);
			}

		}

		// 保存意见到activiti
		taskservice.addComment(taskId, task.getProcessInstanceId(), opinionTxt);
		result.put("isSelectTask", isSelectTask);// 下一步是否选择人员加入的前端的返回值集合中
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> selectTask(String taskId, String taskDefId, String userId) {

		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new RuntimeException("流程不存在");
		}
		Map<String, Object> result = new HashMap<>();
		// 把判断条件设置到流程分支里
		runtimeService.setVariableLocal(task.getExecutionId(), ActivitiConstant.GATEWAY_CONDITION_VARIABLE_NAME,
				taskDefId);

		taskDefId = processUtilService.getNextTask(taskId);
		if (taskDefId != null) {

			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult();

			Map<String, Object> tasksConfig = (Map<String, Object>) getProcessConfig(processDefinition)
					.get("tasksConfig");
			// 获取下一个节点的节点配置信息
			Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(taskDefId);
			// 获取当前节点的节点配置信息
			Map<String, Object> currentTaskConfig = (Map<String, Object>) tasksConfig.get(task.getTaskDefinitionKey());
			List<Map<String, Object>> paramsList = new ArrayList<Map<String, Object>>();
			paramsList.add(taskConfig);
			List userList = null;
			if (taskConfig.containsKey("__DontSelectCurrentTaskUser")
					|| currentTaskConfig.containsKey("__DontSelectUser")) {
				userList = new ArrayList<ActivitiUserVo>();
				ActivitiUserVo user = new ActivitiUserVo();
				user.setUserId(userId);
				user.setUserName("已选择预设用户");
				userList.add(user);
			} else {
				try {
					Map<String, Object> taskUsers = (Map<String, Object>) roleService
							.getNextNode(userId, null, true, paramsList).getModel();
					userList = (List) taskUsers.get("users");
					if (userList == null || userList.size() == 0)
						throw new RuntimeException();
				} catch (Exception e) {
					throw new RuntimeException("角色配置错误");
				}
			}
			// 当前节点类型 0普通节点，1 多人节点
			String userTaskType = Objects.toString(taskConfig.get("__userTaskType"), "0");
			result.put("userTaskType", userTaskType);
			result.put("taskId", taskId);
			result.put("users", userList);
		} else {
			result.put("taskId", taskId);
			result.put("isEnd", true);
		}
		return result;
	}

	/**
	 * 获取流程配置信息
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getProcessConfig(ProcessDefinition processDefinition) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> tasksConfig = new HashMap<String, Object>();

		StartFormData data = formService.getStartFormData(processDefinition.getId());

		for (FormProperty fp : data.getFormProperties()) {
			if (ActivitiConstant.ALL_FORM_DETAIL.equals(fp.getId())) {
				resultMap = JSONObject.parseObject(fp.getValue(), Map.class);
			} else if (ActivitiConstant.TASKS_CONFIG.equals(fp.getId())) {
				tasksConfig = JSONObject.parseObject(fp.getValue(), Map.class);
			}
		}
		resultMap.put("tasksConfig", tasksConfig);
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> startProcess(String companyId, String userId, String key, Map<String, Object> params,
			String nextAssignee, String next, String wyyId) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 定义流程变量并保存一下步骤
		Map<String, Object> flowVariable = new HashMap<String, Object>();
		// 设置该流程为起草状态
		flowVariable.put(ActivitiConstant.PROCESS_STATUS, ActivitiConstant.PROCESS_STATUS_DRAFT);
		flowVariable.put(ActivitiConstant.NEXT_NODE, next);
		// 迭代获取表单流程变量加入到定义流程变量中
		Map formRunVal = (Map) params.get("paramterData");
		flowVariable.putAll(formRunVal);

		String processInstanceId = runtimeService.startProcessInstanceByKey(key, companyId, flowVariable).getId();

		Task task = taskservice.createTaskQuery().processInstanceId(processInstanceId).singleResult();

		if (task != null) {
			task.setAssignee(userId);
			task.setOwner(userId);
			taskservice.saveTask(task);
			taskservice.setVariablesLocal(task.getId(), params);
		}
		Map nextActivity = processUtilService.getNextActivity(task.getId());
		List<FlowNode> flowNodes = (List<FlowNode>) nextActivity.get("flowNodes");
		// String taskDefKey = processUtilService.getNextTask(task.getId());
		if ((boolean) nextActivity.get("needUserSelect")) {// 如果需要选择下一步的流转方向
			List<Map> nextNodes = new ArrayList<>();
			for (FlowNode f : flowNodes) {
				Map n = new HashMap<>();
				n.put("taskDefId", f.getId());
				n.put("taskName", f.getName());
				nextNodes.add(n);
			}
			result.put("nextNodes", nextNodes);// 把下一步的节点放入返回中
		} else {
			if (flowNodes != null && flowNodes.size() > 0) {
				String taskDefKey = flowNodes.get(0).getId();
				ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(task.getProcessDefinitionId()).singleResult();

				Map<String, Object> tasksConfig = (Map<String, Object>) getProcessConfig(processDefinition)
						.get("tasksConfig");
				// 获取下一个节点的节点配置信息
				Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(taskDefKey);
				taskConfig.put("id", taskDefKey);
				List<Map<String, Object>> paramsList = new ArrayList<Map<String, Object>>();
				paramsList.add(taskConfig);

				Map<String, Object> taskUsers = (Map<String, Object>) roleService
						.getNextNode(userId, null, true, paramsList).getModel();
				if (taskUsers == null || taskUsers.get("users") == null)
					throw new RuntimeException("角色配置错误");
				List userList = (List) taskUsers.get("users");

				// 当前节点类型 0普通节点，1 多人节点
				String userTaskType = Objects.toString(taskConfig.get("__userTaskType"), "0");
				result.put("userTaskType", userTaskType);
				result.put("taskId", task != null ? task.getId() : null);
				result.put("users", userList);
				// 插入发起表
				addStartMessage(userId, userId, processDefinition.getName(), companyId, processInstanceId, task.getId(),
						wyyId);
			} else {
				result.put("taskId", task != null ? task.getId() : null);
			}
		}
		return result;
	}

	/**
	 * 插入一条发起消息
	 * 
	 * @param userId
	 * @param startUserId
	 * @param approvalName
	 * @param companyId
	 * @param processInstanceId
	 * @param taskId
	 */
	public String addStartMessage(String userId, String startUserId, String approvalName, String companyId,
			String processInstanceId, String taskId, String wyyId) {
		String currDateStr = DateUtil.getDateStr(new Date());
		String id = UUID.randomUUID().toString();
		ProcessInstance p = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();

		String typeId = p.getProcessDefinitionId().split(":")[0];
		ApprovalType approvalType = typeMapper.selectByPrimaryKey(typeId);
		if (approvalType != null) {
			ThirdApprovalStart start = new ThirdApprovalStart();
			start.setId(id);
			start.setApprovalName(approvalName);
			start.setCompanyId(companyId);
			start.setUserId(userId);
			start.setRunId("act_" + processInstanceId);
			start.setStartDate(currDateStr);
			switch (approvalType.getScene()) {
			case 3:
				start.setLink("/mobile/moblicApprove/toMyFlowDetail-e3.do?taskId=" + taskId);
				break;
			case 4:
				start.setLink("/mobile/moblicApprove/toMyFlowDetail-e4.do?taskId=" + taskId);
				break;
			default:
				break;
			}

			start.setUserId(startUserId);
			start.setStatus("1");
			start.setWyyId(wyyId);
			startMapper.insertSelective(start);
		}
		return id;
	}

	@Override
	public Map<String, Object> getHisProcessValues(String taskId) {
		Map<String, Object> resultMap = new HashMap<>();// 返回的map
		HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		if (hisTask == null)
			throw new RuntimeException("没有找到该任务");
		// 查询历史流程的全局变量并封装
		List<HistoricVariableInstance> hisProcValues = historyService.createHistoricVariableInstanceQuery()
				.excludeTaskVariables().executionId(hisTask.getProcessInstanceId()).list();
		for (HistoricVariableInstance hv : hisProcValues) {
			if (!hv.getVariableName().startsWith("__")) {
				resultMap.put(hv.getVariableName(), hv.getValue());
			}
		}
		return resultMap;
	}

}
