package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.ActivitiHisSequenceFlow;

public interface ActivitiHisSequenceFlowMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActivitiHisSequenceFlow record);

    int insertSelective(ActivitiHisSequenceFlow record);

    ActivitiHisSequenceFlow selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActivitiHisSequenceFlow record);

    int updateByPrimaryKey(ActivitiHisSequenceFlow record);
}