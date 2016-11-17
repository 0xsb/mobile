package cmcc.mobile.admin.service;

import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;

/**
 *
 * @author renlinggao
 * @Date 2016年8月8日
 */
public interface WorkFlowManagerService {
	/**
	 * 获取一个流程的所有的参数
	 * @param processDefinition
	 * @return
	 */
	public Map<String, Object> getWorkFlowParams(ProcessDefinition processDefinition);
	
	/**
	 * 获取流程的表单
	 * @param workFlowParams
	 * @return
	 */
	public Map<String, Object> getForms(Map<String, Object> workFlowParams);
	
	/**
	 * 获取流程的节点配置
	 * @param workFlowParams
	 * @return
	 */
	public Map<String, Object> getTasksConfig(Map<String, Object> workFlowParams);
	
	
}
