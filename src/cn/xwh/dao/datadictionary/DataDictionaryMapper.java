package cn.xwh.dao.datadictionary;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.DataDictionary;

public interface DataDictionaryMapper {
	
	/**
	 * 根据状态编码获取 状态集合
	 * @param typeCode
	 * @return
	 * @throws Exception
	 */
	public List<DataDictionary> getDataDictionaryList(@Param("typeCode") String typeCode) throws Exception;
}
