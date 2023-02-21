package nanoit.kr.db;

import nanoit.kr.domain.PropertyDto;
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
import java.util.concurrent.ConcurrentHashMap;

public class DataBaseSessionManagerBefore {

    public final ConcurrentHashMap<PropertyDto, SqlSessionFactory> dbSessionList;


    public DataBaseSessionManagerBefore() {
        this.dbSessionList = new ConcurrentHashMap<>();
    }

    public void registerDbSessionList(PropertyDto dto, SqlSessionFactory sessionFactory) {
        dbSessionList.put(dto, sessionFactory);
    }

    public void makeSqlSessionFactory(PropertyDto dto) {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(dto.getDbDriver());
        pooledDataSource.setUrl(dto.getUrl());
        pooledDataSource.setUsername(dto.getDbUsername());
        pooledDataSource.setPassword(dto.getDbPwd());
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
        configuration.setMapUnderscoreToCamelCase(true);

        String mapperLocation = dto.getDatabase() + ".xml";
        try (InputStream inputStream = Resources.getResourceAsStream(mapperLocation)) {
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, mapperLocation, configuration.getSqlFragments());
            mapperBuilder.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        registerDbSessionList(dto, sessionFactory);
    }

    public SqlSession getSession(PropertyDto dto) {
        return dbSessionList.get(dto).openSession(true);
    }

}