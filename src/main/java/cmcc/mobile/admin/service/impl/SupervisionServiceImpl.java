package cmcc.mobile.admin.service.impl;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cmcc.mobile.admin.activiti.base.SupervisionConstant;
import cmcc.mobile.admin.activiti.cmd.ExtendTimerCmd;
import cmcc.mobile.admin.base.ActivitiConstant;
import cmcc.mobile.admin.entity.DelegateMsgInfo;
import cmcc.mobile.admin.service.MsgService;
import cmcc.mobile.admin.service.ProcessUtilService;
import cmcc.mobile.admin.service.SupervisionService;
import cmcc.mobile.admin.service.WorkFlowService;

/**
 * 
 * @author chenghaotian
 *
 */
@Service
public class SupervisionServiceImpl implements SupervisionService {

	private static Logger logger = Logger.getLogger(SupervisionService.class);

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

	@Autowired
	private MsgService msgService;

	/** 提前提醒时间单位，当前为1小时的毫秒数 */
	private static long notifyTimeUnit = 60 * 60 * 1000L;

	/** 任务是否即将超时的判断标准，当前为24小时的毫秒数 */
	private static long wwarningDuration = 24 * 60 * 60 * 1000L;

	/** 超时提醒间隔时间，当前为1天 */
	private static String jobSleepInterval = "P0Y0M1DT0H0M0.000S";

	/** 日期时间控件format yyyy-MM-dd HH:mm */
	private static SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * 生成时间表达式(重复次数/开始时间/间隔时间)，例如R2/2016-08-29T23:59:00/P0Y0M0DT0H1M0.000S
	 * 
	 * @param times
	 *            一共提醒多少次(包含最后一次到期提醒)
	 * @param endDdate
	 *            到期时间
	 * @param durationInMilliSeconds
	 *            提醒间隔时间(毫秒)
	 * @return
	 */
	private List<String> getTimeCycleExpression(Integer times, Date endDdate, Long durationInMilliSeconds) {
		List<String> expression = new ArrayList<>();
		// 计算间隔时间的表达式
		Duration duration = null;
		duration = new Duration(durationInMilliSeconds);

		// 计算定时器开始时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDdate);
		cal.setTimeInMillis(cal.getTimeInMillis() - durationInMilliSeconds * (times - 1));

		expression.add("R" + times);
		expression.add(ActivitiConstant.isoDateFormat.format(cal.getTime()));
		expression.add(duration.toString());
		return expression;
	}

	private void addParamterDataTo(Map<String, Object> paramterDataTo, String paramterName, String value) {
		Map<String, Object> paramter = new HashMap<>();
		paramter.put("readOnly", true);
		paramter.put("value", value);
		paramterDataTo.put(paramterName, paramter);
	}

	private static String formatPeriod(Period period) {
		PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears()
				.appendSuffix("年")
				.appendMonths()
				.appendSuffix("月")
				.appendDays()
				.appendSuffix("天")
				.appendHours()
				.appendSuffix("小时")
				.appendMinutes()
				.appendSuffix("分钟")
				.toFormatter();
		return formatter.print(period);
	}

	@Override
	public List<String> getAssigneeOnSupervisionStart(DelegateExecution execution, String mainVarName,
			String subVarName) {
		String processInstanceId = execution.getProcessInstanceId();
		String collection = execution.getVariable(SupervisionConstant.VARIABLE_MULTIINSTANCE_COLLECTION, String.class);

		// 不是首次进入，直接返回给activiti
		if (StringUtils.isNotEmpty(collection)) {
			return JSON.parseObject(collection, new TypeReference<List<String>>() {
			});
		}

		// 查询变量
		Map<String, Object> processVariables = runtimeService.getVariablesLocal(processInstanceId,
				Arrays.asList(mainVarName, subVarName));
		Map<String, Object> newVariables = new HashMap<>();

		List<Map<String, Object>> total = new ArrayList<>();

		// 解析主办人和协办人的json
		String mainJson = Objects.toString(processVariables.get(mainVarName), null);
		String subJson = Objects.toString(processVariables.get(subVarName), null);

		// 解析选人控件json
		if (StringUtils.isNotEmpty(mainJson)) {
			List<Map<String, Object>> mainList = getUserList(mainJson);
			newVariables.put(SupervisionConstant.PARAMTER_MAJOR_USER, mainJson);
			total.addAll(mainList);
		}

		if (StringUtils.isNotEmpty(subJson)) {
			List<Map<String, Object>> subList = JSON.parseObject(subJson,
					new TypeReference<List<Map<String, Object>>>() {
					});
			newVariables.put(SupervisionConstant.PARAMTER_MINOR_USER, subJson);
			total.addAll(subList);
		}

		List<String> assigneeList = new ArrayList<>();
		for (Map<String, Object> map : total) {
			String id = Objects.toString(map.get("userId"), null);
			List<String> itemList = new ArrayList<>();
			itemList.add(id);
			assigneeList.add(JSON.toJSONString(itemList));
		}
		newVariables.put(SupervisionConstant.VARIABLE_MULTIINSTANCE_COLLECTION, JSON.toJSONString(assigneeList));
		newVariables.put(SupervisionConstant.VARIABLE_SUPERVISION_COMPLETED, "0");
		runtimeService.setVariablesLocal(processInstanceId, newVariables);
		return assigneeList;
	}

	private List<Map<String, Object>> getUserList(String userJson) {
		if (StringUtils.isNotEmpty(userJson)) {
			List<Map<String, Object>> userList = JSON.parseObject(userJson,
					new TypeReference<List<Map<String, Object>>>() {
					});
			return userList;
		}
		return new ArrayList<>();
	}

	@Override
	public String getTimerEndDate(DelegateExecution execution, String endDateVariableName, String hourVariableName)
			throws ParseException {
		Map<String, Object> processVariables = runtimeService.getVariablesLocal(execution.getProcessInstanceId(),
				Arrays.asList(endDateVariableName, hourVariableName));

		String endDateStr = Objects.toString(processVariables.get(endDateVariableName), null);
		Date endDate = inputDateFormat.parse(endDateStr);
		int hour = Integer.parseInt(Objects.toString(processVariables.get(hourVariableName), null));

		// 设置开始时间和结束时间变量
		Map<String, Object> variables = new HashMap<>();
		variables.put(SupervisionConstant.PARAMTER_START_TIME, inputDateFormat.format(new Date()));
		variables.put(SupervisionConstant.PARAMTER_END_TIME, endDateStr);
		runtimeService.setVariablesLocal(execution.getProcessInstanceId(), variables);

		// 格式化日期
		List<String> expression = getTimeCycleExpression(2, endDate, hour * notifyTimeUnit);

		return StringUtils.join(expression, "/");
	}

	@Override
	public void notifyOnSupervisionExperied(DelegateExecution execution) throws ParseException {
		TimerEntity job = (TimerEntity) managementService.createJobQuery()
				.processInstanceId(execution.getProcessInstanceId())
				.singleResult();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(execution.getProcessDefinitionId())
				.singleResult();

		logger.debug("督办任务提醒:流程实例id" + execution.getProcessInstanceId());
		List<String> expression = Arrays.asList(job.getRepeat().split("/"));
		String repeatTimes = expression.get(0);

		String mainJson = runtimeService.getVariableLocal(execution.getProcessInstanceId(),
				SupervisionConstant.PARAMTER_MAJOR_USER, String.class);
		List<Map<String, Object>> userList = getUserList(mainJson);

		String endTimeStr = runtimeService.getVariableLocal(execution.getProcessInstanceId(),
				SupervisionConstant.PARAMTER_END_TIME, String.class);
		String template = null;
		String message = null;
		List<String> userIds = new ArrayList<>();
		for (Map<String, Object> user : userList) {
			userIds.add(user.get("userId").toString());
		}
		// TODO 短信提醒
		switch (repeatTimes) {
		case "R1":
			processUtilService.changeTimerToSleepMode(job, jobSleepInterval);
			// 到期
			template = "您的督办任务{0}已于{1}过期,请及时办理";
			message = MessageFormat.format(template, processDefinition.getName(), endTimeStr);
			break;
		case "R":
			// 超时
			Period experiedPeriod = new Duration(inputDateFormat.parse(endTimeStr).getTime(),
					System.currentTimeMillis()).toPeriod(PeriodType.standard());

			template = "您的督办任务{0}已过期{1},请及时办理";
			message = MessageFormat.format(template, processDefinition.getName(), formatPeriod(experiedPeriod));
			break;
		case "R2":
			// 即将到期
			Period timeLeftPeriod = ISOPeriodFormat.standard()
					.parsePeriod(expression.get(2))
					.toStandardDuration()
					.toPeriod();
			template = "您的督办任务{0}即将在{1}后过期,请及时办理";
			message = MessageFormat.format(template, processDefinition.getName(), formatPeriod(timeLeftPeriod));
			break;
		default:
			break;
		}

		logger.debug("向下列用户:" + userIds + "发送消息:" + message);
		// 发送消息
		// msgService.checkdSendMsgBatch(userIds, message);
	}

	@Override
	public Map<String, Object> getRequestExtensionForm(String taskId, String extensionTaskKey) {
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		List<Map<String, Object>> forms = new ArrayList<>();
		List<Map<String, Object>> fromsData = new ArrayList<>();
		Map<String, Object> paramterDataTo = new HashMap<>();

		Task task = processUtilService.getTask(taskId);
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询开始时间和结束时间
		List<String> variableNames = new ArrayList<>();
		variableNames.add(SupervisionConstant.PARAMTER_START_TIME);
		variableNames.add(SupervisionConstant.PARAMTER_END_TIME);
		Map<String, Object> variablesLocal = runtimeService.getVariablesLocal(task.getProcessInstanceId(),
				variableNames);
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId)
				.singleResult();

		// 根据id获取任务的表单详情
		Map<String, String> startFormData = processUtilService.getStartFormData(processDefinitionId);
		Map<String, Object> taskConfig = processUtilService.getTaskConfig(startFormData, extensionTaskKey);
		Map<String, Object> formDetail = processUtilService.getFormDetail(startFormData,
				Objects.toString(taskConfig.get("__formId"), null));
		forms.add(formDetail);

		// 按钮属性
		Map<String, Object> submitButton = new HashMap<>();
		submitButton.put("name", "提交");
		submitButton.put("url", "process/requestExtension.do");
		List<Map<String, Object>> buttons = new ArrayList<>();
		buttons.add(submitButton);

		// 设置返回前台的参数值
		addParamterDataTo(paramterDataTo, SupervisionConstant.PARAMTER_NAME, processDefinition.getName());
		addParamterDataTo(paramterDataTo, SupervisionConstant.PARAMTER_START_TIME,
				Objects.toString(variablesLocal.get(SupervisionConstant.PARAMTER_START_TIME), null));
		addParamterDataTo(paramterDataTo, SupervisionConstant.PARAMTER_END_TIME,
				Objects.toString(variablesLocal.get(SupervisionConstant.PARAMTER_END_TIME), null));

		data.put("forms", forms);
		data.put("fromsData", fromsData);
		data.put("paramterDataTo", paramterDataTo);
		data.put("buttons", buttons);

		model.put("methodType", 1);
		model.put("data", data);
		return model;
	}

	@Override
	public void requestExtension(String taskId, String userId, String model, String reason, String companyId,
			String wyyId) {
		Task task = processUtilService.getTask(taskId);
		Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
		Execution multiInstance = runtimeService.createExecutionQuery()
				.executionId(execution.getParentId())
				.singleResult();
		Execution subProcess = runtimeService.createExecutionQuery()
				.executionId(multiInstance.getParentId())
				.singleResult();
		Map<String, Object> variables = new HashMap<>();
		Map<String, Object> msgInfo = new HashMap<>();
		msgInfo.put("owner", task.getOwner());
		msgInfo.put("companyId", companyId);
		msgInfo.put("wyyId", wyyId);
		msgInfo.put("taskId", task.getId());
		msgInfo.put("userId", userId);
		msgInfo.put("model", model);
		List<String> assigneeList = new ArrayList<>();
		assigneeList.add(task.getAssignee());
		variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(msgInfo));
		variables.put(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE, JSON.toJSONString(assigneeList));
		runtimeService.setVariablesLocal(subProcess.getParentId(), variables);

		// 触发上级分支的事件
		runtimeService.messageEventReceived("endDateChangeEvent", subProcess.getId());
	}

	@Override
	public void changeEndDate(DelegateTask task, String endDateVariableName, String hourVariableName)
			throws ParseException {
		String processInstanceId = task.getProcessInstanceId();

		// 查询延期时间
		Map<String, Object> processVariables = runtimeService.getVariablesLocal(processInstanceId,
				Arrays.asList(SupervisionConstant.PARAMTER_EXTENSION_TIME, hourVariableName));
		String extensionTimeStr = Objects.toString(processVariables.get(SupervisionConstant.PARAMTER_EXTENSION_TIME),
				null);
		Date extensionTime = inputDateFormat.parse(extensionTimeStr);
		int hour = Integer.parseInt(Objects.toString(processVariables.get(hourVariableName), null));

		// 修改流程实例变量
		Map<String, Object> variables = new HashMap<>();
		variables.put(SupervisionConstant.PARAMTER_END_TIME, extensionTimeStr);
		variables.put(endDateVariableName, extensionTimeStr);
		runtimeService.setVariablesLocal(processInstanceId, variables);

		// 更新job状态
		Job job = managementService.createJobQuery().processInstanceId(processInstanceId).singleResult();
		if (job == null) {
			return;
		}
		List<String> expression = getTimeCycleExpression(2, extensionTime, hour * notifyTimeUnit);
		managementService.executeCommand(new ExtendTimerCmd(job.getId(), expression));

	}

	@SuppressWarnings("unchecked")
	@Override
	public void completeOnTaskCreated(DelegateTask task) throws InterruptedException {
		DelegateExecution execution = task.getExecution();
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(execution);
		String assignee = msgInfo.getAssignee().get(0);

		// 设置执行人，归属人和任务表单变量
		task.setAssignee(assignee);
		task.setOwner(msgInfo.getOwner());
		task.setVariablesLocal(msgInfo.getModel());
		Map<String, Object> paramterData = (Map<String, Object>) msgInfo.getModel().get("paramterData");

		// 设置下一步人员信息
		Map<String, Object> variables = new HashMap<>();
		variables.put("owner", msgInfo.getOwner());
		variables.put("companyId", msgInfo.getCompanyId());
		variables.put("wyyId", msgInfo.getWyyId());
		variables.put("taskId", task.getId());
		variables.put("userId", msgInfo.getUserId());
		execution.setVariableLocal(ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO, JSON.toJSONString(variables));
		// 完成任务
		execution.getEngineServices().getTaskService().complete(task.getId(), paramterData);
	}

	@Override
	public Boolean isSupervisionCanComplete(DelegateExecution execution, String mainVarName, String subVarName) {
		String mainJson = runtimeService.getVariableLocal(execution.getProcessInstanceId(), mainVarName, String.class);
		List<Map<String, Object>> mainList = JSON.parseObject(mainJson, new TypeReference<List<Map<String, Object>>>() {
		});

		// 查询多实例下其他分支,如果有主办人所在的分支未完成则不结束多实例
		RuntimeService txRuntimeService = execution.getEngineServices().getRuntimeService();
		List<Execution> list = txRuntimeService.createExecutionQuery().parentId(execution.getParentId()).list();
		List<TaskEntity> tasks = new ArrayList<>();
		for (Execution sibling : list) {
			// 跳过正在提交的分支和已完成分支
			if (sibling.getId().equals(execution.getId()) || !((ExecutionEntity) sibling).isActive()) {
				continue;
			}

			// 根据当前分支的顺序判断是否为主办人所在分支
			Integer loopCounter = Integer
					.parseInt(txRuntimeService.getVariableLocal(sibling.getId(), "loopCounter").toString());
			if (loopCounter < mainList.size()) {
				return false;
			}
			ExecutionEntity executionEntity = (ExecutionEntity) sibling;
			tasks.addAll(executionEntity.getTasks());
		}

		// 更新所有未完成任务的待办信息
		for (TaskEntity taskEntity : tasks) {
			String thirdDealId = taskEntity.getVariableLocal(ActivitiConstant.VARIABLE_TASK_THIRD_DEAL_ID,
					String.class);
			workFlowService.updateDealMessage(thirdDealId, execution.getProcessInstanceId(), "2", null);
		}
		return true;
	}

	@Override
	public void onSupervisionEnd(DelegateExecution execution) {
		String processInstanceId = execution.getProcessInstanceId();
		Job job = managementService.createJobQuery().executionId(execution.getId()).singleResult();
		if (job != null) {
			managementService.deleteJob(job.getId());
		}
		List<String> variableNames = Arrays.asList(ActivitiConstant.VARIABLE_NEXT_TASK_ASSIGNEE,
				ActivitiConstant.VARIABLE_NEXT_TASK_MSG_INFO);
		// 传递变量信息到上级分支
		Map<String, Object> subProcessVariables = execution.getVariablesLocal(variableNames);
		RuntimeService delegateRuntimeService = execution.getEngineServices().getRuntimeService();
		delegateRuntimeService.setVariablesLocal(execution.getParentId(), subProcessVariables);
		// 设置督办状态为已完成
		delegateRuntimeService.setVariableLocal(processInstanceId, SupervisionConstant.VARIABLE_SUPERVISION_COMPLETED,
				"1");

		// 清除没有同意的延期任务,同时更新对应待办记录状态为完成
		List<Task> list = execution.getEngineServices()
				.getTaskService()
				.createTaskQuery()
				.processInstanceId(execution.getProcessInstanceId())
				.list();
		for (Task task : list) {
			TaskEntity taskEntity = (TaskEntity) task;
			if (taskEntity.isDeleted()) {
				continue;
			}
			String thirdDealId = execution.getEngineServices().getTaskService().getVariableLocal(task.getId(),
					ActivitiConstant.VARIABLE_TASK_THIRD_DEAL_ID, String.class);
			workFlowService.updateDealMessage(thirdDealId, processInstanceId, "2", null);
			taskEntity.getExecution().destroyScope("deleted");
		}
	}

	@Override
	public List<Map<String, Object>> getSupversionList(String companyId) throws ParseException {
		List<String> variableNames = Arrays.asList(SupervisionConstant.PARAMTER_MAJOR_USER,
				SupervisionConstant.PARAMTER_START_TIME, SupervisionConstant.PARAMTER_END_TIME);
		// 查询流程定义key
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);
		// 目前只查询任务督办下的流程定义
		params.put("wyyId", "wyy0003");
		Set<String> processDefinitionKeys = processUtilService.getProcessDefinitionKeys(params);

		// 查询还未完成的督办任务
		List<Map<String, Object>> result = new ArrayList<>();
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKeys(processDefinitionKeys)
				.variableValueEquals(SupervisionConstant.VARIABLE_SUPERVISION_COMPLETED, "0")
				.list();

		for (ProcessInstance processInstance : processInstances) {
			Map<String, Object> item = new HashMap<>();
			Map<String, Object> processVariables = runtimeService.getVariablesLocal(processInstance.getId(),
					variableNames);
			String endTimeStr = Objects.toString(processVariables.get(SupervisionConstant.PARAMTER_END_TIME), null);
			Date endTime = inputDateFormat.parse(endTimeStr);
			item.put("processInstanceId", processInstance.getId());
			item.put("name", processInstance.getProcessDefinitionName());
			item.put("majorUser", processVariables.get(SupervisionConstant.PARAMTER_MAJOR_USER));
			item.put("startTime", processVariables.get(SupervisionConstant.PARAMTER_START_TIME));
			item.put("endTime", endTimeStr);

			// 判断是否超时
			long duration = endTime.getTime() - System.currentTimeMillis();
			String status = null;
			if (duration > wwarningDuration) {
				status = "noraml";
			} else if (duration > 0) {
				status = "warning";
			} else {
				status = "expired";
			}
			item.put("duration", duration);
			item.put("status", status);
			result.add(item);
		}

		return result;
	}

	@Override
	public void assignByCandidatesOnTaskCreated(DelegateTask task) {
		DelegateExecution execution = task.getExecution();
		ProcessDefinition processDefinition = execution.getEngineServices()
				.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId())
				.singleResult();
		DelegateMsgInfo msgInfo = new DelegateMsgInfo(execution);
		Map<String, Object> taskConfig = processUtilService.getTaskConfig(task.getProcessDefinitionId(),
				task.getTaskDefinitionKey());
		String candidateUsersJson = Objects.toString(taskConfig.get("candidateUsers"));
		List<Map<String, Object>> userList = JSON.parseObject(candidateUsersJson,
				new TypeReference<List<Map<String, Object>>>() {
				});
		String assignee = Objects.toString(userList.get(0).get("id"), null);
		assignee = processUtilService.formatAssigneeWildcard(assignee, msgInfo);

		// 设置任务执行者
		task.setAssignee(assignee);
		task.setOwner(msgInfo.getOwner());

		// 添加代办信息
		String thirdDealId = workFlowService.addAgentMessage((TaskEntity) task, processDefinition, msgInfo.getCompanyId(),
				msgInfo.getWyyId());
		task.setVariableLocal(ActivitiConstant.VARIABLE_TASK_THIRD_DEAL_ID, thirdDealId);
	}

}
