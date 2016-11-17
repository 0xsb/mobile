package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.Organization;

public interface OrganizationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);
    
    List<Organization> selectAllDept();
    
    Organization getDeptByFullName(String orgFullname);
    
    List<Organization> selectAllDeptByCompanyId(String companyId);
    
    List<Organization> getOrgByPreId(List<String> list);

	List<Organization> selectByCompanyId(String companyId);

	List<Organization> selectByCidandPreId(Organization tAppOrganization);

	List<Organization> selectByCompany(String companyId);

	List<Organization> findByParentId(String orgId);

	List<Organization> selectByOrg(String id);
}