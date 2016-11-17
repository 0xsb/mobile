package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.vo.ActivitiRoleVo;
import cmcc.mobile.admin.vo.ApprovalVo;
import cmcc.mobile.admin.vo.ApprovalWyyVo;

public interface ApprovalMapper {

	List<ApprovalVo> selectByName(ApprovalVo record) ;

	List<Map> selectByValue(ApprovalVo record) ;

	List<ApprovalVo> selectByJson(ApprovalVo record) ;

	List<ApprovalWyyVo> selectByWyy(String companyId) ;

	List<ApprovalWyyVo> selectByImage() ;

}
