package cmcc.mobile.admin.service;

import java.util.List;


import cmcc.mobile.admin.entity.LinkResources;

public interface LinkResiucesService {
	
	public void insert(LinkResources linkResources);
	
	public void deleteById(Long id);
	
	public void updateById(LinkResources linkResources);
	
	public List<LinkResources> selectList(LinkResources linkResources);
	
	public LinkResources  selectByPrimaryKey(Long id);
	
	public LinkResources selectSingle(LinkResources record);
	
}
