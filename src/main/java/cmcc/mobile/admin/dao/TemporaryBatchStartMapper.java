package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.TemporaryBatchStart;

public interface TemporaryBatchStartMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(TemporaryBatchStart record);

	int insertSelective(TemporaryBatchStart record);

	TemporaryBatchStart selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(TemporaryBatchStart record);

	int updateByPrimaryKey(TemporaryBatchStart record);
	
	/**
	 * 根据消息表的主键查询临时表数据
	 * @param thirdId
	 * @return
	 */
	TemporaryBatchStart findByThirdId(String thirdId);
	
	/**
	 * 通过任务id和消息id更新flowId
	 * @param record
	 * @return
	 */
	int updateByThirdId(TemporaryBatchStart record);
}