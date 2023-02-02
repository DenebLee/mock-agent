package nanoit.kr.db;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DataBaseSessionManager {
    private final SqlSessionFactory sqlSessionFactory;

    public DataBaseSessionManager(Properties properties) throws IOException {

        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUsername(properties.getProperty("username"));
        pooledDataSource.setPassword(properties.getProperty("password"));
        pooledDataSource.setPoolMaximumActiveConnections(5);
        pooledDataSource.setPoolMaximumIdleConnections(5);
        pooledDataSource.setPoolMaximumLocalBadConnectionTolerance(11);
        pooledDataSource.setPoolTimeToWait(20 * 1000);
        pooledDataSource.setPoolPingEnabled(true);
        pooledDataSource.setPoolPingQuery("SELECT 1");
        pooledDataSource.setPoolPingConnectionsNotUsedFor(10 * 1000);

        JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, pooledDataSource);

        Configuration configuration = new Configuration(environment);
        configuration.setJdbcTypeForNull(JdbcType.VARCHAR);
        configuration.setCallSettersOnNulls(true);

        String[] mapperResources;
        if (properties.getProperty("mapper").contains(",")) {
            mapperResources = properties.getProperty("mapper").split(",");
        } else {
            mapperResources = new String[]{properties.getProperty("mapper")};
        }
        for (String mapper : mapperResources) {
            InputStream inputStream = Resources.getResourceAsStream(mapper);
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, mapper, configuration.getSqlFragments());
            mapperBuilder.parse();
        }

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public SqlSession getSqlSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }
}
