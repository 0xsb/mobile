package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.base.VirtualActorConstant;
import cmcc.mobile.admin.dao.ActivitiHistoryTaskMapper;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.entity.ActivitiHistoryTask;
import cmcc.mobile.admin.entity.DelegateMsgInfo;
import cmcc.mobile.admin.entity.ExecutionInfo;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.util.ProcessUtil;

/**
 * 
 * @author chenghaotian
 *
 */
@Service
public class processUtilServiceImpl implements ProcessUtilService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskservice;

	@Autowired
	private FormService formService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private ApprovalTypeMapper aprovalTypeMapper;

	@Autowired
	private ActivitiHistoryTaskMapper activitiHistoryTaskMapper;

	@Override
	public Task getTask(String taskId) {
		Task task = taskservice.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new RuntimeException("未查询到任务");
		}
		return task;
	}

	@Override
	public Map<String, String> getStartFormData(String processDefinitionId) {
		Map<String, String> result = new HashMap<>();
		StartFormData startFormData = formService.getStartFormData(processDefinitionId);
		for (FormProperty formProperty : startFormData.getFormProperties()) {
			result.put(formProperty.getId(), formProperty.getValue());
		}
		return result;
	}

	@Override
	public Map<String, Object> getTaskConfig(String processDefinitionId, String taskDefinitionKey) {
		return getTaskConfig(getStartFormData(processDefinitionId), taskDefinitionKey);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskConfig(Map<String, String> startFormData, String taskDefinitionKey) {
		String value = startFormData.get(ActivitiConstant.TASKS_CONFIG);
		Map<String, Object> tasksConfig = JSON.parseObject(value, new TypeReference<Map<String, Object>>() {
		});
		// 获取节点配置信息
		Map<String, Object> taskConfig = (Map<String, Object>) tasksConfig.get(taskDefinitionKey);
		taskConfig.put("id", taskDefinitionKey);
		return taskConfig;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getFormDetail(Map<String, String> startFormData, String formId) {
		String value = startFormData.get(ActivitiConstant.ALL_FORM_DETAIL);
		JSONObject allForms = (JSONObject) JSON.parse(value);
		JSONObject formJson = allForms.getJSONObject("forms").getJSONObject(formId);
		Map<String, Object> formDetail = JSON.parseObject(formJson.toString(),
				new TypeReference<Map<String, Object>>() {
				});// TODO
		return formDetail;
	}

	@Override
	public List<ActivitiHistoryTask> getHistoryTasks(String processInstanceId) {
		return activitiHistoryTaskMapper.selectByProcessInstanceId(processInstanceId);
	}

	@Override
	public Map<String, Object> getNextActivity(String taskId) {
		Map<String, Object> result = new HashMap<>();
		TaskEntity task = (TaskEntity) taskservice.createTaskQuery().taskId(taskId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
		UserTask flowElement = (UserTask) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
		Map<String, Object> variables = taskservice.getVariables(task.getId());
		List<FlowNode> flowNodes = ProcessUtil.getNextTask(bpmnModel, flowElement, variables, true);

		result.put("needUserSelect", flowNodes.size() > 1);
		result.put("flowNodes", flowNodes);
		return result;
	}

	/**
	 * 获取下一个节点的信息
	 * 
	 * @param taskId
	 * @return
	 */
	@Override
	public String getNextTask(String taskId) {
		TaskEntity task = (TaskEntity) taskservice.createTaskQuery().taskId(taskId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
		UserTask flowElement = (UserTask) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
		Map<String, Object> variables = taskservice.getVariables(task.getId());
		UserTask userTask = ProcessUtil.getNextTask(bpmnModel, flowElement, variables);
		if (userTask != null) {
			return userTask.getId();
		}
		return null;
	}

	@Override
	public String getNextTask(DelegateExecution execution) {
		BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
		FlowNode flowElement = (FlowNode) bpmnModel.getFlowElement(execution.getCurrentActivityId());
		Map<String, Object> variables = runtimeService.getVariables(execution.getId());
		UserTask userTask = ProcessUtil.getNextTask(bpmnModel, flowElement, variables);
		if (userTask != null) {
			return userTask.getId();
		}
		return null;
	}

	/**
	 * 获取前一个节点的历史任务
	 * 
	 * @param taskId
	 * @return
	 */
	@Override
	public List<HistoricTaskInstance> getPerviousTasks(String taskId) {
		HistoricTaskInstance current = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		ExecutionInfo info = ExecutionInfo.getExecutionInfo(historyService, managementService,
				current.getProcessInstanceId(), false);
		List<ExecutionInfo> tasks = info.getPerviousTasks(taskId);
		List<HistoricTaskInstance> result = new ArrayList<>();
		for (ExecutionInfo executionInfo : tasks) {
			HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
					.taskId(executionInfo.getId())
					.singleResult();
			result.add(taskInstance);
		}
		return result;
	}

	@Override
	public void changeTimerToSleepMode(TimerEntity job, String interval) {
		List<String> expression = new ArrayList<>();
		expression.add("R");
		expression.add(ActivitiConstant.isoDateFormat.format(job.getDuedate()));
		expression.add(interval);
		job.setRepeat(StringUtils.join(expression, "/"));
		job.setDuedate(null);
	}

	@Override
	public String formatAssigneeWildcard(String temp, DelegateMsgInfo msgInfo) {
		String assignee = temp;
		switch (assignee) {
		case VirtualActorConstant.ACT_PROMOTER_ROLE:
			// 修改任务执行人为流程发起者
			assignee = msgInfo.getOwner();
			break;
		default:
			break;
		}
		return assignee;
	}

	@Override
	public Set<String> getProcessDefinitionKeys(Map<String, Object> params) {
		return aprovalTypeMapper.selectIds(params);
	}

	@Override
	public Map<String, Object> getServiceVariables(Map<String, Object> variables, Map<String, Object> formdetail) {
		// 获取bean对象变量名与表单变量名关联关系
		Map<String, String> relations = (Map<String, String>) formdetail.get("");// TODO
		Set<String> keys = relations.keySet();
		// Map<String, Object> variables =
		// runtimeService.getVariablesLocal(processInstanceId, keys);
		Map<String, Object> result = new HashMap<>();

		for (String key : keys) {
			result.put(relations.get(key), variables.get(key));
		}

		return result;
	}

	public void setServiceVariables(Map<String, Object> variables, Map<String, Object> formdetail) {
		// 获取bean对象变量名与表单变量名关联关系
		Map<String, String> relations = (Map<String, String>) formdetail.get("");// TODO
		Set<String> keys = relations.keySet();
		// Map<String, Object> variables =
		// runtimeService.getVariablesLocal(processInstanceId, keys);
		Map<String, Object> temp = new HashMap<>(variables);

		for (String key : keys) {
			String formAlias = relations.get(key);
			if (variables.containsKey(formAlias)) {
				variables.remove(formAlias);
				variables.put(key, temp.get(formAlias));
			}
		}
	}

}
