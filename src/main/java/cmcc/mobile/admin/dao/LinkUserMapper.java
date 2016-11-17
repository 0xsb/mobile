package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.LinkUser;

public interface LinkUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LinkUser record);

    int insertSelective(LinkUser record);

    LinkUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LinkUser record);

    int updateByPrimaryKey(LinkUser record);
    
    LinkUser selectSingle(LinkUser record);
    
    List<LinkUser> selectList(LinkUser record);
}