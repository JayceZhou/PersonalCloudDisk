package edu.usst.jayce.server.configation;

import javax.sql.*;

import edu.usst.jayce.server.util.ConfigureReader;
import org.springframework.jdbc.datasource.*;
import org.springframework.context.annotation.*;
import org.mybatis.spring.*;
import org.springframework.beans.factory.annotation.*;
import org.mybatis.spring.mapper.*;
import java.io.*;
import org.springframework.core.io.*;

// 服务器部分数据接入设置
@Configurable
public class DataAccess {
	private static Resource[] mapperFiles;
	private static Resource mybatisConfig;

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(ConfigureReader.instance().getFileNodePathDriver());
		ds.setUrl(ConfigureReader.instance().getFileNodePathURL());
		ds.setUsername(ConfigureReader.instance().getFileNodePathUserName());
		ds.setPassword(ConfigureReader.instance().getFileNodePathPassWord());
		return (DataSource) ds;
	}

	@Bean(name = { "sqlSessionFactory" })
	@Autowired
	public SqlSessionFactoryBean sqlSessionFactoryBean(final DataSource ds) {
		final SqlSessionFactoryBean ssf = new SqlSessionFactoryBean();
		ssf.setDataSource(ds);
		ssf.setConfigLocation(DataAccess.mybatisConfig);
		ssf.setMapperLocations(DataAccess.mapperFiles);
		return ssf;
	}

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {
		final MapperScannerConfigurer msf = new MapperScannerConfigurer();
		msf.setBasePackage("edu.usst.jayce.server.mapper");
		msf.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return msf;
	}

	static {
		final String mybatisResourceFolder = ConfigureReader.instance().getPath() + File.separator + "mybatisResource"
				+ File.separator;
		final String mapperFilesFolder = mybatisResourceFolder + "mapperXML" + File.separator;
		DataAccess.mapperFiles = new Resource[] { new FileSystemResource(mapperFilesFolder + "NodeMapper.xml"),
				new FileSystemResource(mapperFilesFolder + "FolderMapper.xml"),
				new FileSystemResource(mapperFilesFolder + "PropertiesMapper.xml") };
		DataAccess.mybatisConfig = (Resource) new FileSystemResource(mybatisResourceFolder + "mybatis.xml");
	}
}
