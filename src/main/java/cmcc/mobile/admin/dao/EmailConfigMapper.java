package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.EmailConfig;

public interface EmailConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EmailConfig record);

    int insertSelective(EmailConfig record);

    EmailConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmailConfig record);

    int updateByPrimaryKey(EmailConfig record);

	EmailConfig selectByUserId(EmailConfig config);
}