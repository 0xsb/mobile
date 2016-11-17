package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.OfficialDocumentType;

public interface OfficialDocumentTypeMapper {
	int deleteByPrimaryKey(Long id);

	int insert(OfficialDocumentType record);

	int insertSelective(OfficialDocumentType record);

	OfficialDocumentType selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(OfficialDocumentType record);

	int updateByPrimaryKey(OfficialDocumentType record);

	OfficialDocumentType findByTypeId(OfficialDocumentType record);
}