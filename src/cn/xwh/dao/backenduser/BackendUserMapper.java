package cn.xwh.dao.backenduser;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.BackendUser;

/**
 * 后台用户接口
 * @author xwh
 *
 */
public interface BackendUserMapper {
	
	/**
	 * 后台用户登录方法
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public BackendUser getLoginUser(@Param("userCode") String userCode) throws Exception;
}
