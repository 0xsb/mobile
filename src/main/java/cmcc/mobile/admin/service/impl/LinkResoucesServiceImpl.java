package cmcc.mobile.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.LinkResourcesMapper;
import cmcc.mobile.admin.entity.LinkResources;
import cmcc.mobile.admin.service.LinkResiucesService;

@Service
public class LinkResoucesServiceImpl implements LinkResiucesService{

	@Autowired
	private LinkResourcesMapper linkResourcesMapper;
	
	@Override
	public void insert(LinkResources linkResources) {
		// TODO Auto-generated method stub
		linkResourcesMapper.insert(linkResources);
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		LinkResources linkResources = new LinkResources();
		linkResources.setId(id);
		linkResources.setFlag(0);
		linkResourcesMapper.updateByPrimaryKey(linkResources);
	}

	@Override
	public void updateById(LinkResources linkResources) {
		// TODO Auto-generated method stub
		linkResourcesMapper.updateByPrimaryKeySelective(linkResources);
	}

	@Override
	public List<LinkResources> selectList(LinkResources linkResources) {
		// TODO Auto-generated method stub
		List<LinkResources> linkResours=linkResourcesMapper.selectList(linkResources);
		return linkResours;
	}

	@Override
	public LinkResources selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		LinkResources linkResources=linkResourcesMapper.selectByPrimaryKey(id);
		return linkResources;
	}

	@Override
	public LinkResources selectSingle(LinkResources record) {
		// TODO Auto-generated method stub
		LinkResources linkResources=linkResourcesMapper.selectSingle(record);
		return linkResources;
	}

}
