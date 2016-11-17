package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.LinkType;

public interface LinkTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LinkType record);

    int insertSelective(LinkType record);

    LinkType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LinkType record);

    int updateByPrimaryKey(LinkType record);
    
    LinkType selectSingle(LinkType record);
    
    List<LinkType> selectList(LinkType record);
}