package cmcc.mobile.admin.dao;
import cmcc.mobile.admin.entity.UserToken;

public interface UserTokenMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserToken record);

    int insertSelective(UserToken record);

    UserToken selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserToken record);

    int updateByPrimaryKey(UserToken record);
    
    UserToken geUserTokenByToken(UserToken record);
    
}