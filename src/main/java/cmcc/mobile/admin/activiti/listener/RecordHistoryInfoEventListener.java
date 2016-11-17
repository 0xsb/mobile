package cmcc.mobile.admin.activiti.listener;

import java.util.Date;
import java.util.UUID;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiSequenceFlowTakenEvent;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cmcc.mobile.admin.dao.ActivitiHisExecutionMapper;
import cmcc.mobile.admin.dao.ActivitiHisSequenceFlowMapper;
import cmcc.mobile.admin.dao.ActivitiHisTaskInstMapper;
import cmcc.mobile.admin.entity.ActivitiHisExecution;
import cmcc.mobile.admin.entity.ActivitiHisSequenceFlow;
import cmcc.mobile.admin.entity.ActivitiHisTaskInst;

@Component
public class RecordHistoryInfoEventListener implements ActivitiEventListener {

	@Autowired
	ActivitiHisExecutionMapper activitiHisExecutionMapper;

	@Autowired
	private ActivitiHisSequenceFlowMapper activitiHisSequenceFlowMapper;

	@Autowired
	ActivitiHisTaskInstMapper activitiHisTaskInstMapper;

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
		case SEQUENCEFLOW_TAKEN:
			createSequenceFlowRecord(event);
			break;
		case HISTORIC_ACTIVITY_INSTANCE_ENDED:
			endTask(event);
			break;
		case ENTITY_CREATED:
			createExecutionRecord(event);
			break;
		case TASK_CREATED:
			createTaskRecord(event);
			break;
		default:
			break;
		}
	}

	private void createTaskRecord(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
		// 插入历史信息表
		ActivitiHisTaskInst record = new ActivitiHisTaskInst();
		record.setId(taskEntity.getId());
		record.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
		record.setProcessInstanceId(taskEntity.getProcessInstanceId());
		record.setExecutionId(taskEntity.getExecutionId());
		record.setName(taskEntity.getName());
		record.setTaskDefKey(taskEntity.getTaskDefinitionKey());
		record.setCreateTime(taskEntity.getCreateTime());

		activitiHisTaskInstMapper.insertSelective(record);
	}

	private void createExecutionRecord(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		Object entity = entityEvent.getEntity();
		if (entity instanceof ExecutionEntity) {
			ExecutionEntity executionEntity = (ExecutionEntity) entity;

			ActivitiHisExecution record = new ActivitiHisExecution();
			record.setId(executionEntity.getId());
			record.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
			record.setProcessInstanceId(executionEntity.getProcessInstanceId());
			record.setParentId(executionEntity.getParentId());
			record.setCreateTime(Context.getProcessEngineConfiguration().getClock().getCurrentTime());
			activitiHisExecutionMapper.insertSelective(record);
		}
	}

	private void endTask(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		if (!(entityEvent.getEntity() instanceof HistoricActivityInstance)) {
			return;
		}
		HistoricActivityInstance instance = (HistoricActivityInstance) entityEvent.getEntity();
		if (!instance.getActivityType().equals("userTask")) {
			return;
		}
		ActivitiHisTaskInst record = new ActivitiHisTaskInst();
		record.setId(instance.getTaskId());
		record.setEndTime(instance.getEndTime());
		record.setDuration(instance.getDurationInMillis());

		activitiHisTaskInstMapper.updateByPrimaryKeySelective(record);
	}

	private void createSequenceFlowRecord(ActivitiEvent event) {
		ActivitiSequenceFlowTakenEvent takenEvent = (ActivitiSequenceFlowTakenEvent) event;
		ActivitiHisSequenceFlow record = new ActivitiHisSequenceFlow();
		record.setId(UUID.randomUUID().toString());
		record.setActId(takenEvent.getId());
		record.setProcessDefinitionId(takenEvent.getProcessDefinitionId());
		record.setProcessInstanceId(takenEvent.getProcessInstanceId());
		record.setExecutionId(takenEvent.getExecutionId());
		record.setSourceId(takenEvent.getSourceActivityId());
		record.setSourceName(takenEvent.getSourceActivityName());
		record.setSourceType(takenEvent.getSourceActivityType());
		record.setTargetId(takenEvent.getTargetActivityId());
		record.setTargetName(takenEvent.getTargetActivityName());
		record.setTargetType(takenEvent.getTargetActivityType());

		record.setCreateTime(new Date());

		activitiHisSequenceFlowMapper.insertSelective(record);
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

}
