package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.UserApprovalDefData;

public interface UserApprovalDefDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserApprovalDefData record);

    int insertSelective(UserApprovalDefData record);

    UserApprovalDefData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserApprovalDefData record);

    int updateByPrimaryKeyWithBLOBs(UserApprovalDefData record);

    int updateByPrimaryKey(UserApprovalDefData record);
    
    List<UserApprovalDefData> findByconfigIdAndUser(UserApprovalDefData param);
}