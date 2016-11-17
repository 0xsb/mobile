package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cmcc.mobile.admin.activiti.cmd.DeleteHistoricVariableCmd;
import cmcc.mobile.admin.activiti.cmd.RejectTaskCmd;
import cmcc.mobile.admin.activiti.cmd.TakeTransitionCmd;
import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.entity.ActivitiHistoryTask;
import cmcc.mobile.admin.entity.ExecutionInfo;
import cmcc.mobile.admin.service.ProcessService;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.service.WorkFlowService;

/**
 * 
 * @author chenghaotian
 *
 */
@Service
public class ProcessServiceImpl implements ProcessService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskservice;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private ProcessUtilService processUtilService;

	private void deleteExecutionVars(ExecutionInfo current) {
		List<HistoricVariableInstance> variables = new ArrayList<>();

		ExecutionInfo perviousExecution = current.getPervious();
		if (perviousExecution == null) {
			throw new RuntimeException("当前节点无法驳回");
		}
		taskservice.removeVariable(current.getId(), ActivitiConstant.VARIABLE_EXECUTION_ANCHOR);

		List<ExecutionInfo> childExecutions = perviousExecution.getChildExecutions();
		if (perviousExecution.isTask()) {
			variables.add(historyService.createHistoricVariableInstanceQuery()
					.variableName(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR)
					.taskId(perviousExecution.getId())
					.singleResult());
		}

		for (ExecutionInfo executionInfo : childExecutions) {
			variables.addAll(historyService.createHistoricVariableInstanceQuery()
					.variableName(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR)
					.executionId(executionInfo.getId())
					.list());
		}

		managementService.executeCommand(new DeleteHistoricVariableCmd(variables));

	}

	@Override
	public void recovery(Task task, String opinion, Map<String, Object> params, Map<String, Object> msgInfo) {
		ExecutionInfo info = ExecutionInfo.getExecutionInfo(historyService, managementService,
				task.getProcessInstanceId(), false);
		List<ExecutionInfo> perviousTasks = info.getPerviousTasks(task.getId());
		if (perviousTasks == null || perviousTasks.isEmpty()) {
			throw new RuntimeException("当前节点无法驳回");
		}
		ExecutionInfo currentTask = info.findChild(task.getId());
		ExecutionInfo firstTask = info.getFirstTask();

		String taskDefinitionKey = null;
		String pervioustaskId = null;
		List<Map<String, Object>> historicVars = new ArrayList<>();

		// 查询节点历史办理人
		List<String> assigneeList = new ArrayList<>();
		for (ExecutionInfo executionInfo : perviousTasks) {
			// TODO 多实例节点转交后后续节点驳回后存在问题
			HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
					.taskId(executionInfo.getId())
					.singleResult();
			Map<String, Object> localVariable = new HashMap<>();
			HistoricVariableInstance formDataVar = historyService.createHistoricVariableInstanceQuery()
					.taskId(historicTaskInstance.getId())
					.variableName(ActivitiConstant.VARIABLE_FORM_DATA)
					.singleResult();
			localVariable.put(formDataVar.getVariableName(), formDataVar.getValue());
			HistoricVariableInstance paramterDataVar = historyService.createHistoricVariableInstanceQuery()
					.taskId(historicTaskInstance.getId())
					.variableName(ActivitiConstant.VARIABLE_PARAMTER_DATA)
					.singleResult();
			localVariable.put(paramterDataVar.getVariableName(), paramterDataVar.getValue());

			historicVars.add(localVariable);

			assigneeList.add(historicTaskInstance.getAssignee());
			if (StringUtils.isEmpty(taskDefinitionKey)) {
				taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
				List<ExecutionInfo> tasks = info.getPerviousTasks(executionInfo.getId());
				if (!tasks.isEmpty()) {
					pervioustaskId = tasks.get(0).getId();
				}
			}
		}

		// 设置任务表单数据
		taskservice.setVariablesLocal(task.getId(), params);
		// 添加意见
		if (StringUtils.isNotEmpty(opinion)) {
			taskservice.addComment(task.getId(), task.getProcessInstanceId(), opinion);
		}

		// 设置分支变量供下个任务使用
		Map<String, Object> executionVariables = new HashMap<>();
		msgInfo.put("userId", task.getAssignee());
		msgInfo.put("owner", task.getOwner());
		msgInfo.put("taskId", pervioustaskId);

		executionVariables.put(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(msgInfo));
		executionVariables.put(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, JSON.toJSONString(assigneeList));
		runtimeService.setVariablesLocal(task.getExecutionId(), executionVariables);

		// 删除分支记录标记
		deleteExecutionVars(currentTask);

		managementService.executeCommand(new TakeTransitionCmd(task.getExecutionId(), taskDefinitionKey,
				ActivitiConstant.TASK_DELETE_REASON_RECOVERED));

		List<Task> newTaskList = taskservice.createTaskQuery()
				.processInstanceId(task.getProcessInstanceId())
				.taskDefinitionKey(taskDefinitionKey)
				.list();
		// 驳回到第一步时手动插入待办信息
		if (firstTask != null && firstTask.getId().equals(perviousTasks.get(0).getId())) {
			Task newTask = newTaskList.get(0);
			String assignee = assigneeList.get(0);
			taskservice.setAssignee(newTask.getId(), assignee);
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId())
					.singleResult();
			String companyId = Objects.toString(msgInfo.get("companyId"), null);
			String wyyId = Objects.toString(msgInfo.get("wyyId"), null);
			workFlowService.addAgentMessage(newTask, processDefinition, companyId, wyyId);
		}

		// 覆盖之前的任务变量到新的任务上
		if (newTaskList.size() == 1) {
			for (int i = 0; i < newTaskList.size(); i++) {
				Task item = newTaskList.get(i);
				taskservice.setVariablesLocal(item.getId(), historicVars.get(i));
			}
		} else {
			// 驳回到多实例任务时暂时无法设置表单
		}
	}

	@Override
	public void reassign(Task task, String assignee, String opinion, Map<String, Object> variables,
			Map<String, Object> msgInfo) {
		// 设置分支变量供下个任务使用
		Map<String, Object> executionVariables = new HashMap<>();
		msgInfo.put("userId", task.getAssignee());
		msgInfo.put("owner", task.getOwner());
		msgInfo.put("taskId", task.getId());
		List<String> assigneeList = new ArrayList<>();
		assigneeList.add(task.getAssignee());
		executionVariables.put(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(msgInfo));
		executionVariables.put(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, JSON.toJSONString(assigneeList));
		runtimeService.setVariablesLocal(task.getExecutionId(), executionVariables);

		taskservice.setVariablesLocal(task.getId(), variables);

		// 删除当前任务的分支记录标记
		taskservice.removeVariable(task.getId(), ActivitiConstant.VARIABLE_EXECUTION_ANCHOR);
		// 添加意见
		if (StringUtils.isNotEmpty(opinion)) {
			taskservice.addComment(task.getId(), task.getProcessInstanceId(), opinion);
		}

		managementService.executeCommand(new TakeTransitionCmd(task.getExecutionId(), task.getTaskDefinitionKey(),
				ActivitiConstant.TASK_DELETE_REASON_REASSIGNED));
	}

	@Override
	public Task reject(String taskId, String opinion, Map<String, Object> variables) {
		Task task = processUtilService.getTask(taskId);

		taskservice.setVariablesLocal(taskId, variables);
		// 添加意见
		if (StringUtils.isNotEmpty(opinion)) {
			taskservice.addComment(task.getId(), task.getProcessInstanceId(), opinion);
		}
		managementService
				.executeCommand(new RejectTaskCmd(taskId, ActivitiConstant.TASK_DELETE_REASON_REJECTED, variables));
		return task;
	}

	@Override
	public String cancel(String taskId, String opinion) {
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		long count = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.finished()
				.count();
		if (count > 1) {
			throw new RuntimeException("当前任务已被他人处理，无法撤销");
		}

		// 移除未完成任务的分支记录标记
		List<Task> list = taskservice.createTaskQuery().processInstanceId(processInstanceId).list();
		for (Task unfinishedTask : list) {
			taskservice.removeVariableLocal(unfinishedTask.getId(), ActivitiConstant.VARIABLE_EXECUTION_ANCHOR);
		}

		runtimeService.setVariableLocal(processInstanceId, ActivitiConstant.PROCESS_STATUS,
				ActivitiConstant.PROCESS_STATUS_REFUSE);
		// 删除流程实例
		runtimeService.deleteProcessInstance(processInstanceId, ActivitiConstant.TASK_DELETE_REASON_CANCELED);
		return processInstanceId;
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
		List<String> taskIds = workFlowService.getLastHistoryTasks(taskId, list);
		List<Map<String, Object>> tasksFormsData = workFlowService.getTasksFormsData(taskIds, processDefinition);
		model.put("tasksFormsData", tasksFormsData);

		return model;
	}

}
