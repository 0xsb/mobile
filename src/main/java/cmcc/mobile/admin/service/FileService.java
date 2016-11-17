package cmcc.mobile.admin.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import cmcc.mobile.admin.entity.File;


/**
 *
 * @author renlinggao
 * @Date 2016年5月30日
 */
public interface FileService {
	  /**
     * 文件上传
     * @param mr 文件request
     * @return Doc
     */
    File fileUpload(MultipartHttpServletRequest mr, HttpServletRequest request, String num,String userId,String companyId);

    /**
     * 文件下载
     * @param response response
     * @param doc 文件
     */
    java.io.File download(HttpServletResponse response, String name) throws Exception;

    /**
     * 多文件上传
     * @param request
     * @param response
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    List<File> multipleFilesUpload(HttpServletRequest request,HttpServletResponse response,String num,String userId,String companyId) throws Exception;
    
    /**
     * 通过id获取文件信息
     * @param id
     * @return
     */
    File getById(Integer id);
    
    /**
     * 删除文件
     * @param id
     * @param addr
     */
    void delteFile(Integer id,String addr);
}
