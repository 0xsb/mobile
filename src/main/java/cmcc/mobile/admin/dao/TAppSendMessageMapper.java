package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.TAppSendMessage;

public interface TAppSendMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TAppSendMessage record);

    int insertSelective(TAppSendMessage record);

    TAppSendMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TAppSendMessage record);

    int updateByPrimaryKey(TAppSendMessage record);

	int deleteByMessageId(Long id);
}