package samples;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class MyConfiguration {

	private DataSource dataSource;

	@Bean
	public DataSource dataSource() {
		if (null == dataSource) {
			dataSource = new DataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUsername("iktv");
			dataSource.setPassword("1yz789%4#!hjd@");
			dataSource
					.setUrl("jdbc:mysql://114.119.6.150:3306/shiku?autoReconnect=true&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true");

			dataSource.setJmxEnabled(true);
			dataSource.setTestWhileIdle(true);
			dataSource.setTestOnBorrow(true);
			dataSource.setTestOnReturn(true);
			dataSource.setValidationInterval(30000);
			dataSource.setValidationQuery("SELECT 1 FROM DUAL");
			dataSource.setTimeBetweenEvictionRunsMillis(30000);
			dataSource.setMaxActive(100);
			dataSource.setInitialSize(30);
			dataSource.setMaxWait(10000);
			dataSource.setMinEvictableIdleTimeMillis(30000);
			dataSource.setMinIdle(10);
			dataSource.setMaxIdle(30);
			dataSource.setLogAbandoned(false);
			dataSource.setRemoveAbandoned(true);
			dataSource.setRemoveAbandonedTimeout(60);
			dataSource
					.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		}

		return dataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		Resource configLocation = resolver
				.getResource("classpath:/com/shiku/db/mybatis/mybatis-config.xml");
		Resource[] mapperLocations = resolver
				.getResources("classpath*:/com/shiku/db/mybatis/*Mapper.xml");

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		sqlSessionFactoryBean.setConfigLocation(configLocation);
		sqlSessionFactoryBean.setMapperLocations(mapperLocations);

		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public DataSourceTransactionManager tx() {
		return new DataSourceTransactionManager(dataSource());
	}

}
