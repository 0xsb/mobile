package cmcc.mobile.admin.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.base.PersonProcessConstant;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.FileMapper;
import cmcc.mobile.admin.dao.OfficialDocumentTypeMapper;
import cmcc.mobile.admin.dao.OfficialPostingRecordMapper;
import cmcc.mobile.admin.dao.OfficialReceivingRecordMapper;
import cmcc.mobile.admin.dao.ThirdApprovalDealMapper;
import cmcc.mobile.admin.dao.ThirdApprovalStartMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ActivitiHistoryTask;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.DelegateMsgInfo;
import cmcc.mobile.admin.entity.OfficialDocumentType;
import cmcc.mobile.admin.entity.OfficialPostingRecord;
import cmcc.mobile.admin.entity.OfficialReceivingRecord;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.entity.ThirdApprovalStart;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.ActivitiRoleService;
import cmcc.mobile.admin.service.ProcessService;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.service.WorkFlowService;
import cmcc.mobile.admin.util.Base64;
import cmcc.mobile.admin.util.DateUtil;
import cmcc.mobile.admin.util.HttpClientUtil;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.util.QRCodeUtil;
import cmcc.mobile.admin.vo.ActivitiUserVo;
import cmcc.mobile.admin.vo.ThirdOaProcessInfo;
import cmcc.mobile.admin.vo.ThirdOaVisitingInformation;
import cmcc.mobile.admin.vo.UserInfoVo;
import net.glxn.qrgen.core.image.ImageType;
import net.mikesu.fastdfs.FastdfsClient;
import net.mikesu.fastdfs.FastdfsClientFactory;

/**
 *
 * @author renlinggao
 * @Date 2016年7月28日
 */
@Service
public class WorkFlowServiceImpl implements WorkFlowService {
	public static final String REMARK_4_KEY = "urgency";// 紧急程度在流程全局变量中的key
	public static final String REMARK_5_KEY = "secretLevel";// 秘密等级在流程全局变量中的key

	public static final int CODE_WIDTH = 400;// 二维码宽
	public static final int CODE_HEIGHT = 400;// 二维码高
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private FormService formService;

	@Autowired
	
	
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskservice;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ThirdApprovalDealMapper dealMapper;

	@Autowired
	private ActivitiRoleService roleService;

	@Autowired
	private ThirdApprovalStartMapper startMapper;

	@Autowired
	
	
	
	private ProcessUtilService processUtilService;

	@Autowired
	private ProcessService processService;

	@Autowired
	private OfficialPostingRecordMapper officialPostingRecordMapper;

	@Autowired
	private OfficialReceivingRecordMapper officialReceivingRecordMapper;

	@Autowired
	private OfficialDocumentTypeMapper officialDocumentTypeMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ApprovalTypeMapper typeMapper;

	@Autowired
	private FastdfsClientFactory fastdfsFactory;

	@Autowired
	private FileMapper fileMapper;

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getStartNodeInfo(String companyId, String userId, String key) {
		Map<String, Object> result = new HashMap<String, Object>();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key)
				.latestVersion().singleResult();
		if (processDefinition == null)
			throw new RuntimeException("流程没有部署");
		Map<String, Object> flowParams = getProcessConfig(processDefinition);
		// 获取表单配置
		Map<String, Object> forms = (Map<String, Object>) flowParams.get("forms");
		// 获取流程配置信息
		Map<String, Object> tasksConfig = (Map<String, Object>) flowParams.get("tasksConfig");

		List<Map<String, Object>> nextTasks = getStartNodesConfigs(tasksConfig, processDefinition);

		String oneId = (String) nextTasks.get(0).get("id");
		Map<String, Object> nextTask = ((Map<String, Object>) tasksConfig.get(oneId));
		Map<String, Object> nextConfig = new HashMap<String, Object>();
		if (nextTasks.size() == 1 && StringUtils.isEmpty((String) nextTask.get("candidateGroups"))
				&& StringUtils.isEmpty((String) nextTask.get("candidateUsers"))) {
			nextConfig.put("Task", oneId);
		} else {
			// 获取下一步走向
			// Map<String, Object> test = test(companyId, userId, nextTasks,
			// false);
			nextConfig = (Map<String, Object>) roleService.getNextNode(userId, companyId, false, nextTasks).getModel();

			if (nextConfig == null || StringUtils.isEmpty((String) nextConfig.get("Task"))) {
				throw new RuntimeException("没有权限发起流程");
			}
			nextTask = ((Map<String, Object>) tasksConfig.get((String) nextConfig.get("Task")));
		}

		String formId = (String) nextTask.get(ActivitiConstant.FORM_ID);
		String button = (String) nextTask.get(ActivitiConstant.NEXT_BUTTON);

		result.put("form", forms.get(formId));
		result.put("next", nextConfig.get("Task"));
		result.put("button", button);
		return result;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
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

		String taskDefKey = processUtilService.getNextTask(task.getId());
		if (StringUtils.isNotEmpty(taskDefKey)) {
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

		return result;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> agree(String userId, String taskId, Map<String, Object> params, String opinionTxt) {

		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		if (task == null)
			throw new RuntimeException("流程不存在");
		Map<String, Object> result = new HashMap<>();
		// 保存表单数据到流程里面
		taskservice.setVariablesLocal(task.getId(), params);
		// 保存意见到activiti
		// taskservice.addComment(taskId, task.getProcessInstanceId(),
		// opinionTxt);

		String taskDefKey = processUtilService.getNextTask(taskId);

		if (StringUtils.isNotEmpty(taskDefKey)) {

			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult();

			Map<String, Object> tasksConfig = (Map<String, Object>) getProcessConfig(processDefinition)
					.get("tasksConfig");
			// 获取下一个节点的节点配置信息
			Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(taskDefKey);
			// 获取当前节点的节点配置信息
			Map<String, Object> currentTaskConfig = (Map<String, Object>) tasksConfig.get(task.getTaskDefinitionKey());

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
		// 保存意见到activiti
		taskservice.addComment(taskId, task.getProcessInstanceId(), opinionTxt);
		return result;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean selectUser(String dealId, String userId, String taskId, String nextAssignee, String companyId,
			String wyyId) {
		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		if (task == null)
			throw new RuntimeException("流程不存在");
		Map<String, Object> taskParams = taskservice.getVariables(taskId);
		// 获取保存的表单信息
		Map formRunVal = (Map) taskParams.get("paramterData");

		// String nextTask = processUtilService.getNextTask(taskId);
		if (StringUtils.isNotEmpty(nextAssignee)) {
			// 设置分支变量供下个任务使用
			Map<String, Object> variables = new HashMap<>();
			Map<String, Object> msgInfo = new HashMap<>();
			msgInfo.put("userId", userId);
			msgInfo.put("owner", task.getOwner());
			msgInfo.put("companyId", companyId);
			msgInfo.put("wyyId", wyyId);
			msgInfo.put("taskId", task.getId());
			variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(msgInfo));

			// 统一候选人格式
			if (!nextAssignee.startsWith("[")) {
				nextAssignee = "[\"" + nextAssignee + "\"]";
			}
			variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, nextAssignee);

			runtimeService.setVariablesLocal(task.getExecutionId(), variables);
		}
		updateDealMessage(dealId, task.getProcessInstanceId(), "2", null);
		// 设置流程状态为办理中
		runtimeService.setVariable(task.getProcessInstanceId(), ActivitiConstant.PROCESS_STATUS,
				ActivitiConstant.PROCESS_STATUS_CIRCULATION);

		taskservice.complete(taskId, formRunVal);
		// 判断任务是否结束
		HistoricVariableInstance hp = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).variableName(ActivitiConstant.PROCESS_STATUS)
				.singleResult();
		if (hp != null && (((Integer) hp.getValue()) == ActivitiConstant.PROCESS_STATUS_COMPLETE)) {
			updateDealMessage(dealId, task.getProcessInstanceId(), "2", "2");
			updateStartMessage(task.getProcessInstanceId(), "2");
		}
		return true;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public Map<String, Object> taskDetails(String taskId) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 返回的forms
		List<Map<String, Object>> resultForms = new ArrayList<Map<String, Object>>();
		// 返回的表单数据集合
		List<Map> resultFormsData = new ArrayList<Map>();
		// 当前任务的表单
		Map<String, Object> resultCurrForm = new HashMap<String, Object>();
		// 获取当前流程任务实例
		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		// 历史任务
		List<ActivitiHistoryTask> list = null;
		if (task != null) {// 流程没有完结状态
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult();

			Map<String, Object> processConfig = getProcessConfig(processDefinition);
			// 获取流程所有的表单
			Map<String, Object> forms = (Map<String, Object>) processConfig.get("forms");
			// 获取流程所有节点的配置
			Map<String, Object> tasksConfig = (Map<String, Object>) processConfig.get("tasksConfig");
			// 获取当前节点的配置
			Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(task.getTaskDefinitionKey());
			// 当前节点是否继承上一个表单
			boolean success = (boolean) taskConfig.get(ActivitiConstant.CONFIG_SUCCESS);
			// 上个表单是否可以修改
			boolean writAble = (boolean) taskConfig.get(ActivitiConstant.CONFIG_WRITABLE);
			// 获取当前节点不被继承的表单
			String noSuccessFormId = (String) taskConfig.get(ActivitiConstant.NO_SUCCESS_FROM_ID);
			// 查询流程存放的全局变量
			List<HistoricVariableInstance> processValue = historyService.createHistoricVariableInstanceQuery()
					.executionId(task.getProcessInstanceId()).list();
			Map<String, Object> processValueMap = new HashMap<String, Object>();
			// 初始化全局变量
			for (HistoricVariableInstance phv : processValue) {
				processValueMap.put(phv.getVariableName(), phv.getValue());
			}
			// 当前节点的数据
			Map<String, Object> runTaskVal = taskservice.getVariables(taskId);
			List<Map> formDatas = (List<Map>) runTaskVal.get("formData");
			// 获取当前节点的表单id
			String formId = (String) taskConfig.get(ActivitiConstant.FORM_ID);
			list = processUtilService.getHistoryTasks(task.getProcessInstanceId());
			// 要返回的所有变量map
			Map<String, Object> paramtersMap = new HashMap<String, Object>();
			if (runTaskVal != null && formDatas != null) {// 如果当前节点
				for (Map fd : formDatas) {
					String fid = (String) fd.get("id");
					Map form = (Map) forms.get(fid);
					if (StringUtils.isNotEmpty(formId) && formId.equals(fid)) {
						resultCurrForm = form;
						paramtersMap.putAll((Map) form.get("paramters"));
					} else {
						resultForms.add(form);
						// 如果可以编辑上表单 把上个表单的变量名也放入返回map
						if (writAble) {
							paramtersMap.putAll((Map) form.get("paramters"));
						}
					}

				}
				resultFormsData = formDatas;

			} else if (success) {// 如果被继承上个表单并且没有填数据
				// 获取历史任务
				HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				if (hisTask == null) {
					throw new RuntimeException("获取历史任务失败");
				}
				List<HistoricTaskInstance> perviousTasks = processUtilService.getPerviousTasks(taskId);
				// 获取上一个节点表单数据
				for (HistoricTaskInstance pht : perviousTasks) {
					HistoricVariableInstance hv = historyService.createHistoricVariableInstanceQuery()
							.taskId(pht.getId()).variableName("formData").singleResult();
					if (hv == null)
						continue;
					List<Map<String, Object>> formsData = (List<Map<String, Object>>) hv.getValue();

					for (Map<String, Object> fd : formsData) {
						String fid = (String) fd.get("id");
						Map form = (Map) forms.get(fid);
						resultForms.add(form);
						resultFormsData.add(fd);
						if (writAble) {
							paramtersMap.putAll((Map) form.get("paramters"));
						}
					}
				}
				// 设置当前表单的信息
				if (StringUtils.isNotEmpty(formId)) {
					Map<String, Object> currForm = (Map<String, Object>) forms.get(formId);
					resultCurrForm.putAll(currForm);
					paramtersMap.putAll((Map) currForm.get("paramters"));
				}

			} else {// 如果没有继承上一个表单
				// 设置当前表单的信息
				if (StringUtils.isNotEmpty(formId)) {
					Map<String, Object> currForm = (Map<String, Object>) forms.get(formId);
					resultCurrForm.putAll(currForm);
					paramtersMap.putAll((Map) currForm.get("paramters"));
				}
			}

			result = new HashMap<String, Object>();
			// 如果有当前不可继承的表单
			if (StringUtils.isNotEmpty(noSuccessFormId)) {
				result.put("currentNoSuccessForm", forms.get(noSuccessFormId));
			}
			// 要返回的全局变量集合
			Map<String, Object> resultParamterDataToMap = new HashMap<String, Object>();

			for (String mapKey : paramtersMap.keySet()) {
				String value = (String) processValueMap.get(mapKey);
				if (StringUtils.isNotEmpty(value)) {
					Map<String, Object> oneParam = new HashMap<String, Object>();
					oneParam.put("readonly", true);
					oneParam.put("value", processValueMap.get(mapKey));
					resultParamterDataToMap.put(mapKey, oneParam);
				}
			}
			result.put("paramterDataTo", resultParamterDataToMap);
			result.put("forms", resultForms);
			result.put("taskConfig", tasksConfig.get(task.getTaskDefinitionKey()));
			result.put("fromsData", resultFormsData);
			result.put("currTaskForm", resultCurrForm);
			result.put("tasksList", list);

		} else {

			HistoricTaskInstance ht = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
			if (ht == null)
				throw new RuntimeException("流程不存在");

			ProcessDefinition processDefinition = repositoryService.getProcessDefinition(ht.getProcessDefinitionId());
			Map<String, Object> processConfig = getProcessConfig(processDefinition);
			// 获取流程所有的表单
			Map<String, Object> forms = (Map<String, Object>) processConfig.get("forms");
			// 获取流程所有节点的配置
			Map<String, Object> tasksConfig = (Map<String, Object>) processConfig.get("tasksConfig");

			list = processUtilService.getHistoryTasks(ht.getProcessInstanceId());
			List<String> taskIds = getLastHistoryTasks(taskId, list);

			resultForms = new ArrayList<>();
			List<Map<String, Object>> resultFormDatas = new ArrayList<Map<String, Object>>();
			if (taskIds != null && !taskIds.isEmpty()) {
				for (String tid : taskIds) {
					HistoricVariableInstance hisVal = historyService.createHistoricVariableInstanceQuery().taskId(tid)
							.variableName("formData").singleResult();
					List<Map<String, Object>> formData = (List<Map<String, Object>>) hisVal.getValue();
					for (Map fd : formData) {
						String taskDefKey = (String) fd.get("id");
						Map<String, Object> form = (Map<String, Object>) forms.get(taskDefKey);
						resultForms.add(form);
						resultFormDatas.add(fd);
					}

				}
			}
			result = new HashMap<String, Object>();
			result.put("forms", resultForms);
			result.put("fromsData", resultFormDatas);
			result.put("tasksList", list);
			result.put("taskConfig", new HashMap<String, Object>());
			// throw new RuntimeException("流程不存在");
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

	/**
	 * 获取第一步骤的条件
	 * 
	 * @param tasksConfig
	 * @param processDefinition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getStartNodesConfigs(Map<String, Object> tasksConfig,
			ProcessDefinition processDefinition) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		// ProcessDefinitionEntity processDefinitionEntity =
		// (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
		// .getDeployedProcessDefinition(processDefinition.getId());
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
		StartEvent startEvent = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false).get(0);
		SequenceFlow sequenceFlow = startEvent.getOutgoingFlows().get(0);
		FlowElement flowElement = bpmnModel.getFlowElement(sequenceFlow.getTargetRef());

		if (flowElement instanceof ExclusiveGateway) {
			ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
			List<SequenceFlow> outgoingFlows = exclusiveGateway.getOutgoingFlows();

			for (SequenceFlow sf : outgoingFlows) {
				String taskDefKey = sf.getTargetRef();
				Map<String, Object> taskconfig = (Map<String, Object>) tasksConfig.get(taskDefKey);
				taskconfig.put("id", taskDefKey);
				result.add(taskconfig);
			}
		} else if (flowElement instanceof UserTask) {
			String taskDefKey = flowElement.getId();
			Map<String, Object> taskconfig = (Map<String, Object>) tasksConfig.get(taskDefKey);
			taskconfig.put("id", taskDefKey);
			result.add(taskconfig);
		}

		return result;
	}

	// /**
	// * 返回下一个节点用户信息
	// *
	// * @param companyId
	// * @param userId
	// * @param nextTasks
	// * @return
	// */
	// private Map<String, Object> test(String companyId, String userId,
	// List<Map<String, Object>> nextTasks,
	// boolean isBelong) {
	// Map<String, Object> result = new HashMap<String, Object>();
	// Map<String, Object> type = new HashMap<String, Object>();
	// List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
	// Map<String, Object> user = new HashMap<String, Object>();
	// user.put("id", "100001");
	// user.put("userName", "开发测试");
	// users.add(user);
	// result.put("type", "Task1");
	// result.put("users", users);
	// return result;
	// }

	@Override
	public List<String> getLastHistoryTasks(String taskId, List<ActivitiHistoryTask> list) {
		List<String> taskIds = new ArrayList<>();
		// 获取最后一个完成的节点上的所有任务id
		if (list.isEmpty()) {
			taskIds.add(taskId);
		} else {
			for (int i = list.size() - 1; i >= 0; i--) {
				ActivitiHistoryTask item = list.get(i);
				// 跳过被撤销或正在处理的任务
				if (StringUtils.equals(item.getStatusCode(), ActivitiConstant.TASK_DELETE_REASON_CANCELED)
						|| StringUtils.equals(item.getStatusCode(), ActivitiConstant.TASK_DELETE_REASON_DELETED)
						|| item.getEndTime() == null) {
					continue;
				}
				taskIds.add(item.getId().toString());
				break;
			}
		}
		return taskIds;
	}

	/**
	 * 插入一条代办消息
	 * 
	 * @param userId
	 * @param startUserId
	 * @param approvalName
	 * @param companyId
	 * @param processInstanceId
	 * @param taskId
	 */
	public String addAgentMessage(String userId, String startUserId, String approvalName, String companyId,
			String processInstanceId, String taskId, String wyyId) {
		String currDateStr = DateUtil.getDateStr(new Date());
		String id = UUID.randomUUID().toString();
		ThirdApprovalDeal deal = new ThirdApprovalDeal();

		ProcessInstance p = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();
		String typeId = p.getProcessDefinitionId().split(":")[0];
		ApprovalType approvalType = typeMapper.selectByPrimaryKey(typeId);
		if (approvalType != null) {

			deal.setId(id);
			deal.setStatus("1");
			deal.setArriveDate(currDateStr);
			deal.setUserId(userId);
			deal.setApprovalName(approvalName);
			deal.setCompanyId(companyId);
			switch (approvalType.getScene()) {
			case 3:
				deal.setLink("/mobile/moblicApprove/toWorkflowDetail-e3.do?taskId=" + taskId + "&" + "thirdId=" + id);
				break;
			case 4:
				deal.setLink("/mobile/moblicApprove/toWorkflowDetail-e4.do?taskId=" + taskId + "&" + "thirdId=" + id);
			default:
				break;
			}
			deal.setRunId("act_" + processInstanceId);
			deal.setNodeStatus("1");
			deal.setUserStartId(startUserId);
			deal.setWyyId(wyyId);
			dealMapper.insertSelective(deal);
		}
		return id;
	}

	@Override
	public String addAgentMessage(Task task, ProcessDefinition processDefinition, String companyId, String wyyId) {
		// 查询流程类型
		String typeId = processDefinition.getKey();
		ApprovalType approvalType = typeMapper.selectByPrimaryKey(typeId);

		String id = "";
		if (approvalType != null) {
			String currDateStr = DateUtil.getDateStr(new Date());
			id = UUID.randomUUID().toString();
			// 获取流程中的所有变量
			Map<String, Object> processParams = runtimeService.getVariables(task.getProcessInstanceId());
			ThirdApprovalDeal deal = new ThirdApprovalDeal();
			deal.setId(id);
			deal.setStatus("1");
			deal.setArriveDate(currDateStr);
			deal.setUserId(task.getAssignee());
			deal.setApprovalName(processDefinition.getName());
			deal.setCompanyId(companyId);
			deal.setRemark2("");// TODO设置上个审批人
			deal.setRemark3(task.getName());// 设置好当前环节名称
			deal.setRemark4(processParams.get(REMARK_4_KEY) != null ? (String) processParams.get(REMARK_4_KEY) : "");// 获取紧急程度
			deal.setRemark5(processParams.get(REMARK_5_KEY) != null ? (String) processParams.get(REMARK_5_KEY) : "");// 获取秘密程度
			switch (approvalType.getScene()) {
			case 3:
				deal.setLink(
						"/mobile/moblicApprove/toWorkflowDetail-e3.do?taskId=" + task.getId() + "&" + "thirdId=" + id);
				break;
			case 4:
				deal.setLink(
						"/mobile/moblicApprove/toWorkflowDetail-e4.do?taskId=" + task.getId() + "&" + "thirdId=" + id);
			default:
				break;
			}
			deal.setRunId("act_" + task.getProcessInstanceId());
			deal.setNodeStatus("1");
			deal.setUserStartId(task.getOwner());
			deal.setWyyId(wyyId);
			dealMapper.insertSelective(deal);
		}
		return id;
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

	/**
	 * 更新发起的状态
	 * 
	 * @param id
	 * @param status
	 */
	public void updateStartMessage(String processInstanceId, String status) {
		ThirdApprovalStart start = new ThirdApprovalStart();
		start.setRunId("act_" + processInstanceId);
		start.setStatus(status);
		startMapper.updateByRunIdSelective(start);
	}

	/**
	 * 更新办理表
	 * 
	 * @param id
	 * @param nodeStatus
	 * @param status
	 */
	public void updateDealMessage(String id, String processInstanceId, String nodeStatus, String status) {
		ThirdApprovalDeal deal = new ThirdApprovalDeal();
		// 如果任务结束
		if (StringUtils.isNotEmpty(status)) {

			ThirdApprovalDeal deal1 = new ThirdApprovalDeal();
			if (id == null) {// 如果是撤销状态
				deal1.setNodeStatus("2");
			}
			deal1.setRunId("act_" + processInstanceId);
			deal1.setStatus(status);
			dealMapper.updateByRunIdSelective(deal1);
		}
		deal.setId(id);

		deal.setNodeStatus(nodeStatus);

		dealMapper.updateByPrimaryKeySelective(deal);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> getTasksFormsData(List<String> taskIds, ProcessDefinition processDefinition) {
		if (taskIds == null || processDefinition == null) {
			throw new RuntimeException("参数错误");
		}

		Map<String, Object> forms = (Map<String, Object>) getProcessConfig(processDefinition).get("forms");
		// 返回的list集合
		List<Map<String, Object>> resultList = new ArrayList<>();
		for (String id : taskIds) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			List<Map<String, Object>> resultforms = new ArrayList<>();
			List<HistoricVariableInstance> hisVal = historyService.createHistoricVariableInstanceQuery().taskId(id)
					.variableName("formData").list();
			if (hisVal != null && hisVal.size() == 1) {
				HistoricVariableInstance hv = hisVal.get(0);
				List<Map<String, Object>> formsData = (List<Map<String, Object>>) hv.getValue();
				resultMap.put("formsData", formsData);
				for (Map<String, Object> fd : formsData) {
					String fid = (String) fd.get("id");
					Map form = (Map) forms.get(fid);
					resultforms.add(form);
				}
			}
			resultMap.put("forms", resultforms);
			resultList.add(resultMap);
		}

		return resultList;
	}

	@Override
	public void recovery(String thirdDealId, String taskId, String opinion, Map<String, Object> params,
			String companyId, String wyyId) {

		Task task = processUtilService.getTask(taskId);
		// 设置分支变量供下个任务使用
		Map<String, Object> msgInfo = new HashMap<>();
		msgInfo.put("companyId", companyId);
		msgInfo.put("wyyId", wyyId);

		processService.recovery(task, opinion, params, msgInfo);
		// 对接消息
		updateDealMessage(thirdDealId, task.getProcessInstanceId(), "2", null);
	}

	@Override
	public void reassign(String thirdDealId, String taskId, String assignee, String opinion,
			Map<String, Object> variables, String companyId, String wyyId) {
		Task task = processUtilService.getTask(taskId);

		// 设置分支变量供下个任务使用
		Map<String, Object> msgInfo = new HashMap<>();
		msgInfo.put("companyId", companyId);
		msgInfo.put("wyyId", wyyId);

		processService.reassign(task, assignee, opinion, variables, msgInfo);
		// 对接处理表
		updateDealMessage(thirdDealId, task.getProcessInstanceId(), "2", null);
	}

	@Override
	public void reject(String thirdDealId, String taskId, String opinion, Map<String, Object> variables,
			String companyId) {
		Task task = processService.reject(taskId, opinion, variables);

		HistoricVariableInstance hp = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).variableName(ActivitiConstant.PROCESS_STATUS)
				.singleResult();
		if (hp != null && (((Integer) hp.getValue()) == ActivitiConstant.PROCESS_STATUS_COMPLETE)) {
			updateDealMessage(thirdDealId, task.getProcessInstanceId(), "2", "2");
			updateStartMessage(task.getProcessInstanceId(), "2");
		} else {
			updateDealMessage(thirdDealId, task.getProcessInstanceId(), "2", null);
		}
	}

	@Override
	public Map<String, Object> getProcessStartedByMe(String taskId) {
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new RuntimeException("未查询到任务");
		}
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
		Map<String, Object> model = new HashMap<>();
		// 查询历史任务
		List<ActivitiHistoryTask> list = processUtilService.getHistoryTasks(task.getProcessInstanceId());
		model.put("tasksList", list);

		// 查询最后一个节点上的表格数据
		List<String> taskIds = getLastHistoryTasks(taskId, list);
		List<Map<String, Object>> tasksFormsData = getTasksFormsData(taskIds, processDefinition);
		model.put("tasksFormsData", tasksFormsData);

		return model;
	}

	@Override
	public void cancel(String thirdDealId, String taskId, String opinion, String companyId) {
		String processInstanceId = processService.cancel(taskId, opinion);
		// 对接消息表
		updateDealMessage(null, processInstanceId, "2", "2");
		updateStartMessage(processInstanceId, "2");

	}

	/**
	 * 启动一个收文流程
	 * 
	 * @param formData
	 *            发文中的数据变量
	 * @param ownerId
	 *            发起人
	 * @param nextUserId
	 *            下一步办理人
	 * @param typeId
	 *            收文流程定义id
	 * @param companyId
	 * @param wyyId
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void endOfPosting(Map formData, String ownerId, String nextUserId, String typeId, String companyId,
			String wyyId) {
		// 定义流程变量并保存一下步骤
		Map<String, Object> flowVariable = new HashMap<String, Object>();
		// 设置该流程为起草状态
		flowVariable.put(ActivitiConstant.PROCESS_STATUS, ActivitiConstant.PROCESS_STATUS_DRAFT);
		// 迭代获取表单流程变量加入到定义流程变量中
		flowVariable.putAll(formData);

		String processInstanceId = runtimeService.startProcessInstanceByKey(typeId, companyId, flowVariable).getId();

		Task task = taskservice.createTaskQuery().processInstanceId(processInstanceId).singleResult();

		if (task != null) {

			if (StringUtils.isNotEmpty(nextUserId)) {
				// 设置分支变量供下个任务使用
				Map<String, Object> variables = new HashMap<>();
				Map<String, Object> msgInfo = new HashMap<>();
				msgInfo.put("userId", nextUserId);
				msgInfo.put("owner", task.getOwner());
				msgInfo.put("companyId", companyId);
				msgInfo.put("wyyId", wyyId);
				msgInfo.put("taskId", task.getId());
				variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(msgInfo));

				// 统一候选人格式
				if (!nextUserId.startsWith("[")) {
					nextUserId = "[\"" + nextUserId + "\"]";
				}
				variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, nextUserId);

				runtimeService.setVariablesLocal(task.getExecutionId(), variables);
			}

			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionKey(typeId).latestVersion().singleResult();
			if (processDefinition == null)
				throw new RuntimeException("流程没有部署");
			Map<String, Object> flowParams = getProcessConfig(processDefinition);
			// 获取表单配置
			Map<String, Object> forms = (Map<String, Object>) flowParams.get("forms");
			// 获取流程配置信息
			Map<String, Object> tasksConfig = (Map<String, Object>) flowParams.get("tasksConfig");
			Map currTaskConfig = (Map) tasksConfig.get(task.getTaskDefinitionKey());
			String formId = (String) currTaskConfig.get(ActivitiConstant.FORM_ID);// 获取表单的flowId

			List<Map> currFormData = new ArrayList<>();

			Map currForm = (Map) forms.get(formId);
			List<Map> wig = (List<Map>) currForm.get("widgets");

			for (Map c : wig) {// 初始化表单的数据
				String key = (String) c.get("alias");
				Map<String, Object> cData = new HashMap<>();
				cData.put("id", c.get("reName"));
				cData.put("controlName", c.get("controlId"));
				if (StringUtils.isNotEmpty(key)) {
					cData.put("value", formData.get(key) != null ? formData.get(key) : "");
				} else {
					cData.put("value", "");
				}
				currFormData.add(cData);
			}

			Map<String, Object> fd = new HashMap<>();
			fd.put("id", formId);
			fd.put("title", currForm.get("title"));// TODO 标题
			fd.put("data", currFormData);

			List<Map> fds = new ArrayList<>();
			fds.add(fd);

			Map<String, Object> fdsMap = new HashMap<>();
			fdsMap.put("formData", fds);
			fdsMap.put("paramterData", formData);

			task.setAssignee(ownerId);
			task.setOwner(ownerId);
			taskservice.saveTask(task);
			taskservice.setVariablesLocal(task.getId(), fdsMap);

			// 自动完成当前发起人步骤
			taskservice.complete(task.getId());
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public void postingEndEvent(String taskId, String typeId) {
		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		// 是否点击过
		boolean isClick = taskservice.getVariableLocal(taskId, ActivitiConstant.TASK_IS_CLICK, Boolean.class) != null
				? taskservice.getVariableLocal(taskId, ActivitiConstant.TASK_IS_CLICK, Boolean.class) : false;
		if (isClick)
			throw new RuntimeException("封发完成，请不要多次封发");
		// 获取发文流程的数据
		String processInstanceId = task.getProcessInstanceId();
		Map<String, Object> params = runtimeService.getVariables(processInstanceId);// 发文中的流程的全局变量

		String userId = task.getAssignee();// 收文发起人
		// String typeId = "ACT_6e69be22-2c8b-42da-aa51-46f32b772a78";// TODO
		// // 收文的流程id

		String postTypeId = task.getProcessDefinitionId().split(":")[0];

		// String receiptLeaderJson =
		// Objects.toString(params.get("receiptLeader"), "[]");
		// List<Map> leaders = JSON.parseObject(receiptLeaderJson, new
		// TypeReference<List<Map>>() {
		// });
		// 获取流程中相关信息
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(task.getExecutionId(), runtimeService);
		String companyId = msgInfo.getCompanyId();
		String wyyId = msgInfo.getWyyId();
		// 发文的文件类型信息
		OfficialDocumentType docType = new OfficialDocumentType();
		docType.setCompanyId(companyId);
		docType.setTypeId(postTypeId);
		OfficialDocumentType postType = officialDocumentTypeMapper.findByTypeId(docType);
		// 生成发文编号
		OfficialPostingRecord postingRecord = new OfficialPostingRecord();
		postingRecord.setTypeId(postTypeId);
		postingRecord.setCompanyId(companyId);
		postingRecord.setYear(new Date().getYear());
		OfficialPostingRecord currPostingRecord = officialPostingRecordMapper.findByTypeIdAndYear(postingRecord);
		// 计算当前文件的号次
		Long postNum = 1l;
		if (currPostingRecord != null) {
			postingRecord = currPostingRecord;
			postNum = postingRecord.getNumber() + 1;
		}
		// 新建一条发文记录
		postingRecord.setId(null);
		postingRecord.setNumber(postNum);
		postingRecord.setCreateTime(new Date());
		officialPostingRecordMapper.insertSelective(postingRecord);
		// 生成发文编号并保存到收文的数据中
		String issuedNumber = postType.getAbbreviate() + "[" + new Date().getYear() + "]" + postNum;
		params.put("issuedNumber", issuedNumber);

		// 解析获取部门信息
		String orgJson = (String) params.get("mainPerson");
		String orgJson1 = (String) params.get("copyPerson");
		// 启动各部门的收文流程
		if (StringUtils.isNoneEmpty(orgJson)) {
			List<Map> orgList = JSONObject.parseArray(orgJson, Map.class);
			List<Map> orgList1 = JSONObject.parseArray(orgJson1, Map.class);
			orgList.addAll(orgList1);
			for (Map l : orgList) {
				String orgId = (String) l.get("id");
				User user = new User();
				user.setOrgId(orgId);
				user = userMapper.findOrgLeader(user);
				if (user != null) {
					// 收文的文件类型信息
					docType.setTypeId(typeId);
					OfficialDocumentType reType = officialDocumentTypeMapper.findByTypeId(docType);
					// 该部门最后一条收文的记录
					OfficialReceivingRecord receivingRecord = new OfficialReceivingRecord();
					receivingRecord.setCompanyId(companyId);
					receivingRecord.setYear(new Date().getYear());
					receivingRecord.setDept(user.getOrgId());
					OfficialReceivingRecord currRecord = officialReceivingRecordMapper
							.findByDeptAndYear(receivingRecord);
					Long receiveNum = 1l;
					if (currRecord != null) {
						receivingRecord = currRecord;
						receiveNum = currRecord.getNumber() + 1;
					}
					receivingRecord.setCreateTime(new Date());
					receivingRecord.setNumber(receiveNum);
					receivingRecord.setId(null);
					officialReceivingRecordMapper.insertSelective(receivingRecord);
					// 设置该部门收文编号
					String receiptNumber = reType.getAbbreviate() + "[" + new Date().getYear() + "]" + receiveNum;
					params.put("receiptNumber", receiptNumber);
					endOfPosting(params, userId, user.getId(), typeId, companyId, wyyId);

				}
			}
		}
		// 设置按钮为点击过
		taskservice.setVariableLocal(taskId, ActivitiConstant.TASK_IS_CLICK, true);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void reservePersonnelTaked(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		// 获取上个节点保存的信息
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(execution);
		String companyId = msgInfo.getCompanyId();// 公司的id
		// 获取流程相关的信息
		String processInstanceId = task.getProcessInstanceId();// 实例id
		String typeId = task.getProcessDefinitionId().split(":")[0];// 流程的key
		String createUserId = task.getOwner();// 发起用户id
		HistoricProcessInstance hisProcessInstance = execution.getEngineServices().getHistoryService()
				.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		Date reserveTime = hisProcessInstance.getStartTime();// 发起时间（预约时间）
		String tableName = "third_oa_process_info";// 关联的数据库名称
		UserInfoVo userinfo = userMapper.findUserOrgName(createUserId);// 发起人员的信息集合

		// 获取流程全局保存的数据
		Map<String, Object> taskParams = execution.getEngineServices().getRuntimeService()
				.getVariables(task.getProcessInstanceId());
		String unit = (String) taskParams.get(PersonProcessConstant.UNIT);// 访问人员的单位
		String startTimeStr = (String) taskParams.get(PersonProcessConstant.START_TIME);// 开始时间
		String endTimeStr = (String) taskParams.get(PersonProcessConstant.END_TIME);// 结束时间
		String appintmentTimeStr = (String) taskParams.get(PersonProcessConstant.APPOINTMENT_TIME);// 预约哪天
		String amOrPm = (String) taskParams.get(PersonProcessConstant.AM_OR_PM);// 上午下午全天
		List<String> mobiles = (List<String>) taskParams.get(PersonProcessConstant.MOBILES);// 预约人员手机号集合
		List<String> userNames = (List<String>) taskParams.get(PersonProcessConstant.USER_NAMES);// 预约人员名字集合
		String remark = (String) taskParams.get(PersonProcessConstant.REMARK);// 备注信息

		if (mobiles != null && userNames != null && mobiles.size() == userNames.size() && mobiles.size() > 0) {
			Date currDateTime = new Date();// 当前时间点
			// 初始化流程数据
			ThirdOaProcessInfo processInfo = new ThirdOaProcessInfo();
			processInfo.setTypeId(typeId);
			processInfo.setProcessInstanceId(processInstanceId);
			processInfo.setCreateUserId(createUserId);
			processInfo.setCreateTime(currDateTime);
			processInfo.setReserveTime(reserveTime);
			processInfo.setType(0);
			processInfo.setLinkTableName(tableName);
			processInfo.setCompanyId(companyId);
			processInfo.setCreateUserName(userinfo.getUserName());
			processInfo.setCreateUserAllOrgName(userinfo.getOrgAllName());
			processInfo.setField1(userinfo.getMobile());
			// 初始化来访人员记录数据
			List<ThirdOaVisitingInformation> vistInfoList = new ArrayList<>();
			Date startTime = null;
			Date endTime = null;
			if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {// 如果开始时间和结束时间不等于空
				startTime = DateUtil.getDate(startTimeStr, "yyyy-MM-dd HH:mm");
				endTime = DateUtil.getDate(endTimeStr, "yyyy-MM-dd HH:mm");
			} else if (StringUtils.isNotEmpty(appintmentTimeStr) && StringUtils.isNotEmpty(amOrPm)) {
				Date appintmentTime = DateUtil.getDate(appintmentTimeStr, "yyyy-MM-dd");
				if (PersonProcessConstant.TIME_AM.equals(amOrPm)) {
					startTime = new Date(appintmentTime.getTime() + 8 * 60 * 60 * 1000 + 30 * 60 * 1000);// 上午8点半
					endTime = new Date(appintmentTime.getTime() + 12 * 60 * 60 * 1000);// 上午12点
				} else if (PersonProcessConstant.TIME_PM.equals(amOrPm)) {
					startTime = new Date(appintmentTime.getTime() + 13 * 60 * 60 * 1000 + 30 * 60 * 1000);// 下午一点半
					endTime = new Date(appintmentTime.getTime() + 17 * 60 * 60 * 1000 + 30 * 60 * 1000);// 下午5点半
				} else if (PersonProcessConstant.TIME_ALL_DATE.equals(amOrPm)) {
					startTime = new Date(appintmentTime.getTime() + 8 * 60 * 60 * 1000 + 30 * 60 * 1000);// 上午8点半
					endTime = new Date(appintmentTime.getTime() + 17 * 60 * 60 * 1000 + 30 * 60 * 1000);// 下午5点半
				} else {
					throw new RuntimeException("来访时间格式错误");
				}

			} else {
				throw new RuntimeException("预约的时间不能为空");
			}
			String codeUrl = PropertiesUtil.getAppByKey("QR_CODE_URL");// 二维码前缀地址
			String porcessInsertUrl = PropertiesUtil.getAppByKey("THIRD_OA_PROCESS_INFO_INSERT");// 插入流程信息请求的地址
			String personInfoInsertUrl = PropertiesUtil.getAppByKey("THIRD_OA_RERSON_VISIT_INSERT");// 插入人员预约记录请求的地址
			JsonResult prcoResult = postPorcessInfo(porcessInsertUrl, processInfo);
			if (!prcoResult.getSuccess())
				throw new RuntimeException("接口错误：" + prcoResult.getMessage());
			Long oaProcessInfoId = Long.parseLong(prcoResult.getModel() + "");
			FastdfsClient fastdfsClient = fastdfsFactory.getFastdfsClient();
			List<File> files = new ArrayList<>();
			List<cmcc.mobile.admin.entity.File> hyFiles = new ArrayList<>();
			for (int i = 0; i < mobiles.size(); i++) {
				String mobile = mobiles.get(i);// 预约人员手机
				String userName = userNames.get(i);// 预约人员手机
				String codeId = UUID.randomUUID().toString();// 2维码的id
				File file = QRCodeUtil.from(codeUrl + "?id=" + codeId + "\r\n").to(ImageType.JPG)
						.withSize(CODE_WIDTH, CODE_HEIGHT).file(codeId);// 生成二维码
				// 上传到fastdfs
				Map<String, String> meta = new HashMap<String, String>();
				meta.put("companyId", companyId);
				meta.put("fileName", file.getName());
				meta.put("userId", createUserId);
				meta.put("num", "");
				try {
					// 上传图片
					String fileid = fastdfsClient.upload(file, FilenameUtils.getExtension(file.getName()), meta);
					String thumFileId = fastdfsClient.uploadSlave(file, fileid, "128X128",
							FilenameUtils.getExtension(file.getName()));
					files.add(file);
					// 批量插入的文件记录
					cmcc.mobile.admin.entity.File hyFile = new cmcc.mobile.admin.entity.File();
					hyFile.setAddr(Base64.getBase64(fileid));
					hyFile.setCompanyId(companyId);
					hyFile.setCreatetime(currDateTime);
					hyFile.setName(userName + "(" + mobile + ")" + ".jpg");
					hyFile.setUserId(createUserId);
					hyFile.setSize(file.length() + "");
					hyFiles.add(hyFile);
					// 批量插入的表数据
					ThirdOaVisitingInformation vistInfo = new ThirdOaVisitingInformation();
					vistInfo.setOaProcessInfoId(oaProcessInfoId);
					vistInfo.setUnit(unit);
					vistInfo.setUserName(userName);
					vistInfo.setMobile(mobile);
					vistInfo.setCodeId(codeId);
					vistInfo.setCreateTime(currDateTime);
					vistInfo.setStartTime(startTime);
					vistInfo.setEndTime(endTime);
					vistInfo.setStatus(1);// 正常状态
					vistInfo.setUserStatus(0);// 预约状态
					vistInfo.setIsError(0);// 正常状态
					vistInfo.setRemark(remark);
					vistInfo.setIsSendMess(0);// 短信没有发送状态
					vistInfoList.add(vistInfo);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			// 批量插入文件表数据
			fileMapper.insertBatch(hyFiles);

			// 往任务中插入图片信息 TODO（写死以后重新设计）
			List<Map> formData = (List<Map>) execution.getEngineServices().getTaskService().getVariable(task.getId(),
					"formData");
			List<Map> data = (List<Map>) formData.get(0).get("data");
			for (Map cd : data) {
				if ((cd.get("controlName") + "").equals("TableField")) {
					List<Map> tdatas = (List<Map>) cd.get("value");
					for (int i = 0; i < tdatas.size(); i++) {
						List<Map> list = (List<Map>) tdatas.get(i).get("list");
						for (Map ld : list) {
							if ((ld.get("controlName") + "").equals("DDPhotoField")) {
								cmcc.mobile.admin.entity.File f = hyFiles.get(i);
								Map<String, Object> img = new HashMap<>();
								img.put("id", f.getId());
								img.put("name", f.getAddr());
								List<Map> imgList = new ArrayList<>();
								imgList.add(img);
								ld.put("value", JSONObject.toJSONString(imgList));
							}
						}
					}
				}
			}
			execution.getEngineServices().getTaskService().setVariableLocal(task.getId(), "formData", formData);

			// 请求批量插入预约人员数据
			JsonResult perResult = postPersonVistInfos(personInfoInsertUrl, files, vistInfoList);
			if (!perResult.getSuccess())
				throw new RuntimeException("接口错误:" + perResult.getMessage());

		}

	}

	/**
	 * 插入第三方流程信息
	 * 
	 * @param url
	 * @return
	 */
	private JsonResult postPorcessInfo(String url, ThirdOaProcessInfo processInfo) {
		Map<String, String> params = new HashMap<>();
		params.put("typeId", processInfo.getTypeId());
		params.put("processInstanceId", processInfo.getProcessInstanceId());
		params.put("createUserId", processInfo.getCreateUserId());
		params.put("createTime", DateUtil.getDateStr(processInfo.getCreateTime()));
		params.put("reserveTime", DateUtil.getDateStr(processInfo.getReserveTime()));
		params.put("type", processInfo.getType() + "");
		params.put("linkTableName", processInfo.getLinkTableName());
		params.put("companyId", processInfo.getCompanyId());
		params.put("createUserName", processInfo.getCreateUserName());
		params.put("createUserAllOrgName", processInfo.getCreateUserAllOrgName());
		params.put("field1", processInfo.getField1());

		String jsonStr = HttpClientUtil.post(url, params);
		if (StringUtils.isEmpty(jsonStr)) {
			throw new RuntimeException("请求第三方接口异常：（插入流程信息）");
		}
		return JSONObject.parseObject(jsonStr, JsonResult.class);
	}

	/**
	 * 发送批量插入用户预约记录的数据和二维码图片
	 * 
	 * @param url
	 * @param files
	 * @param vistInfos
	 * @return
	 */
	private JsonResult postPersonVistInfos(String url, List<File> files, List<ThirdOaVisitingInformation> vistInfos) {
		Map<String, String> params = new HashMap<>();
		params.put("jasonArray", JSONObject.toJSONString(vistInfos));
		String jsonStr = HttpClientUtil.postFormAndFiles(url, params, files);
		if (StringUtils.isEmpty(jsonStr)) {
			throw new RuntimeException("请求第三方接口异常：（插入流程信息）");
		}
		return JSONObject.parseObject(jsonStr, JsonResult.class);
	}

}
