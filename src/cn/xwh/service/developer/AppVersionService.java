package cn.xwh.service.developer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.AppVersion;

public interface AppVersionService {
	/**
	 * 获得版本信息集合
	 * @return
	 * @throws Exception
	 */
	public List<AppVersion> getAppVersionList(Integer pid) throws Exception;
	/**
	 * 新增版本信息
	 * @return
	 */
	public boolean addAppVersion(AppVersion appversion) throws Exception;	
	
	/**
	 * 获得版本对象,用于修改版本反显信息
	 * @return
	 * @throws Exception
	 */
	public AppVersion getAppVersionById(Integer id)throws Exception;
	
	/**
	 * 修改版本文件
	 * @param id
	 * @return
	 */
	public boolean delAPKFile(Integer id)throws Exception;
	
	/**
	 * 保存修改版本信息
	 * @return
	 */
	public boolean appVersionModify(AppVersion appVersion) throws Exception;
	

	/**
	 * 查询app的版本数量
	 * @return
	 * @throws Exception
	 */
	public int getAppVersionCount(Integer aid);
	
	/**
	 * 删除app版本信息
	 * @return
	 * @throws Exception
	 */
	public int delAppVersionByAppId(Integer aid);
}
