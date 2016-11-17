package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.OfficialReceivingRecord;

public interface OfficialReceivingRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OfficialReceivingRecord record);

    int insertSelective(OfficialReceivingRecord record);

    OfficialReceivingRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OfficialReceivingRecord record);

    int updateByPrimaryKey(OfficialReceivingRecord record);
    
    OfficialReceivingRecord findByDeptAndYear(OfficialReceivingRecord record);
}