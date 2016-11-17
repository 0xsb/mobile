package cmcc.mobile.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.LinkTypeMapper;
import cmcc.mobile.admin.entity.LinkType;
import cmcc.mobile.admin.service.LinkTypeService;

@Service
public class LinkTypeServiceImpl implements LinkTypeService{
	
	@Autowired
	private LinkTypeMapper linkTypeMapper;
	
	public List<LinkType> queryListLinkTypes(LinkType record){
		List<LinkType> types=linkTypeMapper.selectList(record);
		return types;
	}

	@Override
	public void deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		linkTypeMapper.deleteByPrimaryKey(id);
	}

	@Override
	public void insert(LinkType record) {
		// TODO Auto-generated method stub
		linkTypeMapper.insert(record);
	}

	@Override
	public void insertSelective(LinkType record) {
		// TODO Auto-generated method stub
		linkTypeMapper.insertSelective(record);
	}

	@Override
	public LinkType selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		LinkType linkType=linkTypeMapper.selectByPrimaryKey(id);
		return linkType;
	}

	@Override
	public void updateByPrimaryKeySelective(LinkType record) {
		// TODO Auto-generated method stub
		linkTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public void updateByPrimaryKey(LinkType record) {
		// TODO Auto-generated method stub
		linkTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public LinkType selectSingle(LinkType record) {
		// TODO Auto-generated method stub
		LinkType linkType=linkTypeMapper.selectSingle(record);
		return linkType;
	}

}
