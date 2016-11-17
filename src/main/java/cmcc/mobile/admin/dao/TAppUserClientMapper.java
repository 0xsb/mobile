package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.TAppUserClient;

public interface TAppUserClientMapper {
	int deleteByPrimaryKey(Long id);

	int insert(TAppUserClient record);

	int insertSelective(TAppUserClient record);

	TAppUserClient selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TAppUserClient record);

	int updateByPrimaryKey(TAppUserClient record);

	List<TAppUserClient> selectByAccount(Map<String, Object> map);

	List<TAppUserClient> selectByParams(Map<String, Object> map);
}