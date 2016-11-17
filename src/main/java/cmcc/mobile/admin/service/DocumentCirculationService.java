package cmcc.mobile.admin.service;

import java.util.Map;

/**
 *
 * @author renlinggao
 * @Date 2016年9月19日
 */
public interface DocumentCirculationService {
	/**
	 * 公文传阅同意
	 * 
	 * @param userId
	 * @param taskId
	 * @param params
	 * @param opinionTxt
	 * @return
	 */
	public Map<String, Object> agree(String userId, String taskId, Map<String, Object> params, String opinionTxt);

	/**
	 * 主动选择选择任务
	 * 
	 * @param taskId
	 * @param taskDefId
	 * @return
	 */
	public Map<String, Object> selectTask(String taskId, String taskDefId, String userId);
	
	/**
	 * 场景三发起流程
	 * @param companyId
	 * @param userId
	 * @param key
	 * @param params
	 * @param nextAssignee
	 * @param next
	 * @param wyyId
	 * @return
	 */
	public Map<String, Object> startProcess(String companyId, String userId, String key, Map<String, Object> params,
			String nextAssignee, String next, String wyyId);
	
	/**
	 * 查询流程全局的历史变量
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> getHisProcessValues(String taskId);
}
