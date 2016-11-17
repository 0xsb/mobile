package cmcc.mobile.admin.service;

import cmcc.mobile.admin.base.JsonResult;

public interface ProducesService {

	JsonResult getImage();

	JsonResult getProduces(String companyId);

}
