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

    public DataBaseSessionManager(Properties prop) throws IOException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(prop.getProperty("driver"));
        pooledDataSource.setUrl(prop.getProperty("url"));
        pooledDataSource.setUsername(prop.getProperty("username"));
        pooledDataSource.setPassword(prop.getProperty("password"));
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

        // Camel case -> snake Case 자동 매핑
        configuration.setMapUnderscoreToCamelCase(true);

        InputStream inputStream = Resources.getResourceAsStream(prop.getProperty("mapper"));
        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, prop.getProperty("mapper"), configuration.getSqlFragments());
        mapperBuilder.parse();

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    }

    public SqlSession getSqlSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }
}
