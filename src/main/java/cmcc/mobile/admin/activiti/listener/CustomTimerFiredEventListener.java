package cmcc.mobile.admin.activiti.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.jobexecutor.TimerEventHandler;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Component;

import cmcc.mobile.admin.base.ActivitiConstant;

@Component
public class CustomTimerFiredEventListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		EngineServices engineServices = event.getEngineServices();
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		TimerEntity job = (TimerEntity) entityEvent.getEntity();
		System.out.println("triggerTimeEvent测试------------");
		System.out.println(job.getDuedate());

		String nestedActivityId = TimerEventHandler.getActivityIdFromConfiguration(job.getJobHandlerConfiguration());
		ExecutionEntity execution = (ExecutionEntity) engineServices.getRuntimeService().createExecutionQuery()
				.executionId(entityEvent.getExecutionId()).singleResult();
		ActivityImpl borderEventActivity = execution.getProcessDefinition().findActivity(nestedActivityId);

		ScopeImpl parentTask = borderEventActivity.getParent();
		List<Task> taskList = engineServices.getTaskService().createTaskQuery()
				.processInstanceId(entityEvent.getProcessInstanceId()).taskDefinitionKey(parentTask.getId()).list();
		for (Task task : taskList) {
			String thirdDealId = engineServices.getTaskService().getVariableLocal(task.getId(),
					ActivitiConstant.VARIABLE_TASK_THIRD_DEAL_ID, String.class);
			
		}

		List<String> expression = new ArrayList<>();

		// engineServices.getManagementService().executeCommand(new
		// ExtendTimerCmd(job.getId(), expression));

		// job.setRepeat("R2/P0Y0M1DT0H0M0.000S");
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

}
