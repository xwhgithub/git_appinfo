package cn.xwh.service.developer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.AppInfo;

public interface AppInfoService {

	/**
	 * 查询app信息 获得app集合
	 * @param softwareName	app名称
	 * @param status	app状态
	 * @param flatformId	平台
	 * @param categoryLevel1	一级分类
	 * @param categoryLevel2	二级分类
	 * @param categoryLevel3	三级分类
	 * @param from	当前页
	 * @param pageSize	每页记录数
	 * @return 返回app集合
	 * @throws Exception
	 */
	public List<AppInfo> getAppInfoList(
			@Param("softwareName") String softwareName,
			@Param("status")Integer status,
			@Param("flatformId")Integer flatformId,
			@Param("categoryLevel1") Integer categoryLevel1,
			@Param("categoryLevel2") Integer categoryLevel2,
			@Param("categoryLevel3") Integer categoryLevel3,
			@Param("from") Integer from,
			@Param("pageSize") Integer pageSize) throws Exception;
	

	/**
	 * 获取app总记录数
	 * @param softwareName
	 * @param status
	 * @param flatformId
	 * @param categoryLevel1
	 * @param categoryLevel2
	 * @param categoryLevel3
	 * @return
	 * @throws Exception
	 */
	public int getAppInfoCount(
			@Param("softwareName") String softwareName,
			@Param("status")Integer status,
			@Param("flatformId")Integer flatformId,
			@Param("categoryLevel1") Integer categoryLevel1,
			@Param("categoryLevel2") Integer categoryLevel2,
			@Param("categoryLevel3") Integer categoryLevel3)throws Exception;
	
	/**
	 * 添加app
	 * @param appInfo
	 * @return
	 */
	public boolean add(AppInfo appInfo);
	
	/**
	 * 验证APK是否存在
	 * @return
	 */
	public AppInfo getAppInfo(@Param("APKName")String APKName) throws Exception;
}
