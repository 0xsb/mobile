package cmcc.mobile.admin.service;
import java.util.List;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.EmailConfig;
import cmcc.mobile.admin.entity.TAppMessage;
import cmcc.mobile.admin.vo.AppMessageVo;
import cmcc.mobile.admin.vo.PageVo;



public interface TAppMessageService {

	int deleteByPrimaryKey(Long id);
 




	TAppMessage selectByPrimaryKey(Long id);




	JsonResult updateByPrimaryKey(TAppMessage message);



	int save(AppMessageVo record, boolean sendMessage, List<String> strings);


	int updateByPrimaryKeySelective(TAppMessage message);





	JsonResult selectAllParams(AppMessageVo vo);





	JsonResult checkMessageUpdate(TAppMessage vo);





	JsonResult selectByHistroyNotice(String type, String companyId, String title, String beginTime, String endTime,
			String status, PageVo vo);





	JsonResult addEmailUrl(EmailConfig config);





	JsonResult updateEmailUrl(EmailConfig config);





	JsonResult selectEmailUrl(EmailConfig config);







}
