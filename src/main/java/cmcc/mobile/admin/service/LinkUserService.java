package cmcc.mobile.admin.service;

import java.util.List;

import cmcc.mobile.admin.entity.LinkUser;

public interface LinkUserService {
	
	public void insert(LinkUser linkUser);
	
	public void deleteByPrimaryKey(long id);
	
	public void updateByPrimaryKeySelective(LinkUser linkUser);
	
    public LinkUser selectSingle(LinkUser record);
    
    public List<LinkUser> selectList(LinkUser record);
}
