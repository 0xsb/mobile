package cmcc.mobile.admin.dao;

import cmcc.mobile.admin.entity.Customer;

public interface CustomerMapper {
    int deleteByPrimaryKey(String id);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);
    
    Customer selectByCompanyId(String companyId);
}