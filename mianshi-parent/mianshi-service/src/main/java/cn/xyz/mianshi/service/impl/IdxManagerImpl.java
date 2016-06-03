package cn.xyz.mianshi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.mapper.IdxMapper;
import cn.xyz.mianshi.service.IdxManager;
import cn.xyz.mianshi.vo.IdxVO;

@Service
public class IdxManagerImpl implements IdxManager {

	@Autowired
	private IdxMapper idxMapper;

	@Override
	public Integer getUserId() {
		IdxVO idxVO = new IdxVO();
		idxMapper.insert(idxVO);

		return idxVO.getId();
	}

}
