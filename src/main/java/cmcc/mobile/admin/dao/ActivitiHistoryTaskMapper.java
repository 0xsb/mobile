package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cmcc.mobile.admin.entity.ActivitiHistoryTask;

public interface ActivitiHistoryTaskMapper {
	public List<ActivitiHistoryTask> selectByProcessInstanceId(String processInstanceId);
}
