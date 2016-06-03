package cn.xyz.commons.autoconfigure;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "datasource.pool")
public class KDataSourceProperties extends PoolProperties {

	private static final long serialVersionUID = 1L;

}
