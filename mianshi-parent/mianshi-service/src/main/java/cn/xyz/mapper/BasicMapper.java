package cn.xyz.mapper;

import java.util.List;

import cn.xyz.mianshi.vo.AreaVO;
import cn.xyz.mianshi.vo.OptionVO;

public interface BasicMapper {

	List<AreaVO> getAreaList(int parentId);

	List<OptionVO> getOptionList(int parentId);

}
