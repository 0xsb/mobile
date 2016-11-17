package cmcc.mobile.admin.activiti.listener;

import java.util.Objects;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cmcc.mobile.admin.base.ActivitiConstant;

@Component
public class EntityCreatedEventListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		Object entity = entityEvent.getEntity();
		if (entity instanceof ExecutionEntity) {
			ExecutionEntity executionEntity = (ExecutionEntity) entity;
			executionEntity.setVariableLocal(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR, executionEntity.getParentId());

			// 由边界事件创建的分支视为次要分支，生成分支树时父级分支不持有当前分支的引用
			ActivityImpl activity = executionEntity.getActivity();
			if (activity == null) {
				return;
			}
			String activityType = Objects.toString(activity.getProperty("type"), null);
			if (StringUtils.equals(activityType, "boundaryMessage")) {
				executionEntity.setVariableLocal(ActivitiConstant.VARIABLE_EXECUTION_ANCHOR_ADDITIONAL, "boundaryMessage");
			}
		}
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

}
