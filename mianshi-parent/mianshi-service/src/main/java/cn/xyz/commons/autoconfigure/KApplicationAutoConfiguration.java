package cn.xyz.commons.autoconfigure;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter;

@Configuration
public class KApplicationAutoConfiguration {

	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(
				new MappingFastjsonHttpMessageConverter());
	}

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer0() {
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
		configurer.setBasePackage("cn.xyz.mapper");
		configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return configurer;
	}

}
