package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.ActivitiHisTaskInst;

public interface ActivitiHisTaskInstMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActivitiHisTaskInst record);

    int insertSelective(ActivitiHisTaskInst record);

    ActivitiHisTaskInst selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActivitiHisTaskInst record);

    int updateByPrimaryKey(ActivitiHisTaskInst record);
}