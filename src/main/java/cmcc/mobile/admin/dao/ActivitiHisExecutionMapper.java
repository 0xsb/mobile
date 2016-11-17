package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.ActivitiHisExecution;

public interface ActivitiHisExecutionMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActivitiHisExecution record);

    int insertSelective(ActivitiHisExecution record);

    ActivitiHisExecution selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActivitiHisExecution record);

    int updateByPrimaryKey(ActivitiHisExecution record);
}