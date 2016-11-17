package cmcc.mobile.admin.service;

import java.util.List;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.LinkType;

public interface LinkTypeService {
	
	public List<LinkType> queryListLinkTypes(LinkType record);
	
	public void deleteByPrimaryKey(Long id);

    public void  insert(LinkType record);

    public void  insertSelective(LinkType record);

    public LinkType selectByPrimaryKey(Long id);

    public void  updateByPrimaryKeySelective(LinkType record);

    public void  updateByPrimaryKey(LinkType record);
    
    public LinkType selectSingle(LinkType record);
    
}
