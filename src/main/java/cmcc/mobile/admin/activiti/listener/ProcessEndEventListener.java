package cmcc.mobile.admin.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

import cmcc.mobile.admin.base.ActivitiConstant;

/**
 * 流程结束插入流程状态
 * @author renlinggao
 * @Date 2016年8月29日
 */
public class ProcessEndEventListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent entityEvent) {
		entityEvent.getEngineServices().getRuntimeService().setVariableLocal(entityEvent.getProcessInstanceId(),
				ActivitiConstant.PROCESS_STATUS, ActivitiConstant.PROCESS_STATUS_COMPLETE);
	}

	@Override
	public boolean isFailOnException() {

		return true;
	}

}
