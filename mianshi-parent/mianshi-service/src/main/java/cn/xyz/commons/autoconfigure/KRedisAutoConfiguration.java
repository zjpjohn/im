package cn.xyz.commons.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import cn.xyz.commons.support.jedis.JedisTemplate;

@Configuration
public class KRedisAutoConfiguration {

	@Autowired
	private KRedisProperties properties;

	@Bean
	public JedisPoolConfig jdeisPoolConfig() {
		return new JedisPoolConfig();
	}

	@Bean
	public JedisPool jedisPool() {
		return new JedisPool(jdeisPoolConfig(), properties.getHost(), properties.getPort());
	}

	@Bean
	public JedisTemplate jedisTemplate() {
		return new JedisTemplate(jedisPool());
	}

}
