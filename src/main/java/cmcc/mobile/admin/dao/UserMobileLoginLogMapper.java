package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.UserMobileLoginLog;

public interface UserMobileLoginLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserMobileLoginLog record);

    int insertSelective(UserMobileLoginLog record);

    UserMobileLoginLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserMobileLoginLog record);

    int updateByPrimaryKey(UserMobileLoginLog record);
}