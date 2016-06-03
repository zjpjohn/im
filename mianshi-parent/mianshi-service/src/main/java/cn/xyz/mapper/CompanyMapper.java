package cn.xyz.mapper;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.example.CompanyExample;
import cn.xyz.mianshi.vo.CompanyVO;

public interface CompanyMapper {

	int add(CompanyVO company);

	int countByExample(CompanyExample example);

	CompanyVO get(Integer companyId);

	List<CompanyVO> selectByExample(CompanyExample example);

	int update(CompanyVO company);

	int updateOthers(Map<String, Object> parameter);

}