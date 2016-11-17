package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.File;

public interface FileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(File record);

    int insertSelective(File record);

    File selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(File record);

    int updateByPrimaryKey(File record);
    
    /**
     * 批量插入
     * @param list
     * @return
     */
   int insertBatch(List<File> list);
}