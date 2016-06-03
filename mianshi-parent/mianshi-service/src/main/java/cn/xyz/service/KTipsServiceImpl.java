package cn.xyz.service;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cn.xyz.mapper.TipsMapper;

import com.google.common.collect.Maps;

@Component
public class KTipsServiceImpl implements ApplicationContextAware {

	private static Map<Integer, String> tipsMap = Maps.newHashMap();

	public static String getTipsValue(int tipsKey) {
		return tipsMap.get(tipsKey);
	}

	private TipsMapper tipsMapper;

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		tipsMapper = context.getBean(TipsMapper.class);
		tipsMapper.findAll().forEach(tips -> {
			tipsMap.put(tips.getTipsKey(), tips.getTipsValue());
		});
	}

}
