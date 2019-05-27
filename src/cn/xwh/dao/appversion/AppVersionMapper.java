package cn.xwh.dao.appversion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.AppVersion;

public interface AppVersionMapper {
	
	/**
	 * 获得版本信息集合
	 * @return
	 * @throws Exception
	 */
	public List<AppVersion> getAppVersionList(@Param("pid")Integer pid) throws Exception;
		
	/**
	 * 新增版本信息
	 * @return
	 */
	public int addAppVersion(AppVersion appversion) throws Exception;
	
	/**
	 * 获得版本对象,用于修改版本反显信息
	 * @return
	 * @throws Exception
	 */
	public AppVersion getAppVersionById(@Param("id")Integer id)throws Exception;
	
	/**
	 * 修改版本文件
	 * @param id
	 * @return
	 */
	public int delAPKFile(@Param("id")Integer id);
	
	/**
	 * 保存修改版本信息
	 * @return
	 */
	public int appVersionModify(AppVersion appVersion) throws Exception;
	
	/**
	 * 查询app的版本数量
	 * @return
	 * @throws Exception
	 */
	public int getAppVersionCount(@Param("aid")Integer aid)throws Exception;
	
	/**
	 * 删除app版本信息
	 * @return
	 * @throws Exception
	 */
	public int delAppVersionByAppId(@Param("aid")Integer aid) throws Exception;
}	
