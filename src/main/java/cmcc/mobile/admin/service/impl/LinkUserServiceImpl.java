package cmcc.mobile.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.LinkUserMapper;
import cmcc.mobile.admin.entity.LinkUser;
import cmcc.mobile.admin.service.LinkUserService;

@Service
public class LinkUserServiceImpl implements LinkUserService{
	@Autowired
	private LinkUserMapper linkUserMapper;

	@Override
	public void insert(LinkUser linkUser) {
		// TODO Auto-generated method stub
		linkUserMapper.insert(linkUser);
	}

	@Override
	public void deleteByPrimaryKey(long id) {
		// TODO Auto-generated method stub
		linkUserMapper.deleteByPrimaryKey(id);
	}

	@Override
	public void updateByPrimaryKeySelective(LinkUser linkUser) {
		// TODO Auto-generated method stub
		linkUserMapper.updateByPrimaryKeySelective(linkUser);
	}

	@Override
	public LinkUser selectSingle(LinkUser record) {
		// TODO Auto-generated method stub
		LinkUser linkUser =new LinkUser();
		linkUser=linkUserMapper.selectSingle(record);
		return linkUser;
	}

	@Override
	public List<LinkUser> selectList(LinkUser record) {
		// TODO Auto-generated method stub
		List<LinkUser> linkUsers =linkUserMapper.selectList(record);
		return linkUsers;
	}
	
}