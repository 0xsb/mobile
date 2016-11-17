package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.LinkResources;

public interface LinkResourcesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LinkResources record);

    int insertSelective(LinkResources record);

    LinkResources selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LinkResources record);

    int updateByPrimaryKey(LinkResources record);
    
    LinkResources selectSingle(LinkResources record);
    
    List<LinkResources> selectList(LinkResources record);
}