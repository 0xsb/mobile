package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.OfficialPostingRecord;

public interface OfficialPostingRecordMapper {
	int deleteByPrimaryKey(Long id);

	int insert(OfficialPostingRecord record);

	int insertSelective(OfficialPostingRecord record);

	OfficialPostingRecord selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(OfficialPostingRecord record);

	int updateByPrimaryKey(OfficialPostingRecord record);

	OfficialPostingRecord findByTypeIdAndYear(OfficialPostingRecord record);
}