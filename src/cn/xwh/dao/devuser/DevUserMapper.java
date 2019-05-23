package cn.xwh.dao.devuser;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.DevUser;

public interface DevUserMapper {
	
	/**
	 * 开发者登录方法
	 * @param devCode
	 * @return
	 * @throws Exception
	 */
	public DevUser getLoginUser(@Param("devCode")String devCode) throws Exception;
}
