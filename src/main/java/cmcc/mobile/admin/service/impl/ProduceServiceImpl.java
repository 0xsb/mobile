package cmcc.mobile.admin.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ApprovalMapper;
import cmcc.mobile.admin.service.ProducesService;
import cmcc.mobile.admin.vo.ApprovalWyyVo;

@Service
public class ProduceServiceImpl implements ProducesService{
@Autowired
private ApprovalMapper appMapper ;

//获取产品大类
	@Override
	public JsonResult getProduces(String companyId) {
		List<ApprovalWyyVo>	Appwyy = appMapper.selectByWyy(companyId) ;
		if(Appwyy==null){
			return new JsonResult(false,"没有产品信息",null) ;
			
		}
		return new JsonResult(true,"查询成功！",Appwyy);
	}
//获取首页轮播图
	@Override
	public JsonResult getImage() {
		List<ApprovalWyyVo> list = appMapper.selectByImage() ;
		return new JsonResult(true,"查询成功",list);
	}

}
