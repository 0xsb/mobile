package cmcc.mobile.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cmcc.mobile.admin.activiti.base.SupervisionConstant;
import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.DelegateMsgInfo;
import cmcc.mobile.admin.service.ActivitiRoleService;
import cmcc.mobile.admin.service.ProcessDelegateService;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.service.WorkFlowService;
import cmcc.mobile.admin.vo.ActivitiUserVo;

/**
 * 
 * @author chenghaotian
 *
 */
@Service
public class ProcessDelegateServiceImpl implements ProcessDelegateService {

	@Autowired
	private ActivitiRoleService activitiRoleService;

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private ProcessUtilService processUtilService;

	@Override
	public void assignByVarOnTaskCreated(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		ProcessDefinition processDefinition = execution.getEngineServices()
				.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId())
				.singleResult();
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(execution);
		String assignee = msgInfo.getAssignee().get(0);
		assignee = processUtilService.formatAssigneeWildcard(assignee, msgInfo);

		// 设置任务执行人
		task.setAssignee(assignee);
		task.setOwner(msgInfo.getOwner());

		// 添加待办信息
		String thirdDealId = workFlowService.addAgentMessage((TaskEntity)task, processDefinition, msgInfo.getCompanyId(),
				msgInfo.getWyyId());
		task.setVariableLocal(ActivitiConstant.VARIABLE_TASK_THIRD_DEAL_ID, thirdDealId);
	}

	@Override
	public List<String> getAssigneeOnMultiInstanceCreated(DelegateExecution execution) {
		String processInstanceId = execution.getProcessInstanceId();
		String collection = execution.getVariable(SupervisionConstant.VARIABLE_MULTIINSTANCE_COLLECTION, String.class);

		// 不是首次进入，直接返回给activiti
		if (StringUtils.isNotEmpty(collection)) {
			return JSON.parseObject(collection, new TypeReference<List<String>>() {
			});
		}

		String assigneeJson = execution.getVariable(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, String.class);
		List<String> result = new ArrayList<>();
		if (StringUtils.isNotBlank(assigneeJson)) {
			List<String> assigneeList = JSON.parseObject(assigneeJson, new TypeReference<List<String>>() {
			});
			for (String assignee : assigneeList) {
				List<String> itemList = new ArrayList<>();
				itemList.add(assignee);
				result.add(JSON.toJSONString(itemList));
			}
		}
		// 设置多实例用户id到流程实例上
		Map<String, Object> newVariables = new HashMap<>();
		newVariables.put(SupervisionConstant.VARIABLE_MULTIINSTANCE_COLLECTION, JSON.toJSONString(result));
		execution.getEngineServices().getRuntimeService().setVariablesLocal(processInstanceId, newVariables);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setAssigneeOnMultiInstanceTaked(DelegateExecution execution) {
		List<String> assigneeList = new ArrayList<>();
		String nextTaskId = processUtilService.getNextTask(execution);
		Map<String, Object> taskConfig = processUtilService.getTaskConfig(execution.getProcessDefinitionId(),
				nextTaskId);
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(execution);

		// 根据任务节点配置获取用户
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(taskConfig);
		JsonResult result = activitiRoleService.getNextNode(msgInfo.getUserId(), null, true, list);
		if (!result.getSuccess()) {
			throw new RuntimeException(result.getMessage());
		}
		Map<String, Object> model = (Map<String, Object>) result.getModel();
		List<ActivitiUserVo> users = (List<ActivitiUserVo>) model.get("users");

		// 设置变量
		assigneeList.add(users.get(0).getUserId());
		execution.setVariableLocal(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, JSON.toJSONString(assigneeList));
	}

	@Override
	public void setAssigneeWithPerviousOnTaskTaked(DelegateExecution execution) {
		List<String> assigneeList = new ArrayList<>();
		TransitionImpl transition = ((ExecutionEntity) execution).getTransition();

		List<HistoricTaskInstance> historicTasks = execution.getEngineServices()
				.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.taskDefinitionKey(transition.getDestination().getId())
				.executionId(execution.getId())
				.orderByHistoricTaskInstanceEndTime()
				.desc()
				.listPage(0, 1);

		// 设置变量
		assigneeList.add(historicTasks.get(0).getAssignee());
		execution.setVariableLocal(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, JSON.toJSONString(assigneeList));
	}

	@Override
	public void triggerTimeEvent(Job job, DelegateExecution execution) {
		System.out.println("triggerTimeEvent测试------------");
		System.out.println(job.getId());
	}

	@Override
	public void triggerTimeEventEnd(Job job, DelegateExecution execution) {
		System.out.println("triggerTimeEvent测试------------end");
		System.out.println(job.getId());
	}

}
