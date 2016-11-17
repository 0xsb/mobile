package cmcc.mobile.admin.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.stereotype.Component;

import cmcc.mobile.admin.base.ActivitiConstant;

@Component
public class TaskCreatedEventListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;

		Object entity = entityEvent.getEntity();
		if (entity instanceof TaskEntity) {
			TaskEntity taskEntity = (TaskEntity) entity;
			taskEntity.createVariableLocal(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR, taskEntity.getExecutionId());
			// XXX 有自动完成任务事件监听的任务节点无法正常删除这个变量，这种问题应该属于activiti的bug，
			// 先使用下面的方式临时修复
			if (taskEntity.isDeleted()) {
				VariableInstanceEntity variableInstance = taskEntity.getVariableInstances().get(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR);
				variableInstance.delete();
			}

		}
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

}
