package com.github.jjjzzzqqq.mini.db.router.config;

import com.github.jjjzzzqqq.mini.db.router.DBRouterAspect;
import com.github.jjjzzzqqq.mini.db.router.DBRouterConfig;
import com.github.jjjzzzqqq.mini.db.router.dynamic.DynamicSpringDataSource;
import com.github.jjjzzzqqq.mini.db.router.strategy.IDBRouterStrategy;
import com.github.jjjzzzqqq.mini.db.router.strategy.impl.IDBRouterStrategyHashImpl;
import com.github.jjjzzzqqq.mini.db.router.util.PropertyUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据源自动配置类 <br><br>
 * 通过该配置类，将所有配置文件中对应的数据库对应的数据源，添加到Spring中管理
 */
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    /**
     * 数据源配置组
     * key:配置名
     * value:配置值
     */
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    /**
     * 默认数据源配置
     * key:配置名
     * value:配置值
     */
    private Map<String, Object> defaultDataSourceConfig;

    /**
     * 分库数量
     */
    private int dbCount;

    /**
     * 分表数量
     */
    private int tbCount;

    /**
     * 路由字段
     */
    private String routerKey;


    @Bean
    public DataSource dataSource() {

        //创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();

        for (String dbName : dataSourceMap.keySet()) {
            Map<String, Object> propertyMap = dataSourceMap.get(dbName);
            String url = propertyMap.get("url").toString();
            String username = propertyMap.get("username").toString();
            String password = propertyMap.get("password").toString();
            targetDataSources.put(dbName, new DriverManagerDataSource(url, username, password));
        }

        //设置数据源
        DynamicSpringDataSource dynamicDataSource = new DynamicSpringDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(new DriverManagerDataSource(defaultDataSourceConfig.get("url").toString(), defaultDataSourceConfig.get("username").toString(), defaultDataSourceConfig.get("password").toString()));

        return dynamicDataSource;
    }

    @Bean
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(dbCount, tbCount, routerKey);
    }


    /**
     * 配置路由策略 <br>
     * 此处可拓展为根据配置文件选择的路由方式 <br>
     * 注入不同的策略实现类
     */
    @Bean
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig) {
        return new IDBRouterStrategyHashImpl(dbRouterConfig);
    }


    /**
     * 当不存在db-router-aspect Bean的时候注入切面<br>
     * 用来给用户自定义拓展切面逻辑
     */
    @Bean(name = "db-router-aspect")
    @ConditionalOnMissingBean
    public DBRouterAspect point(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return new DBRouterAspect(dbRouterConfig, dbRouterStrategy);
    }


    /**
     * 配置事务管理器
     */
    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }

    /**
     * 通过Spring回调获取配置文件参数
     */
    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "mini-db-router.jdbc.datasource.";

        dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
        tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
        routerKey = environment.getProperty(prefix + "routerKey");

        //将数据源List添加到dataSourceMap 和 defaultDataSourceConfig
        String dataSources = environment.getProperty(prefix + "list");

        //多数据源
        assert dataSources != null;
        for (String dbName : dataSources.split(",")) {
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbName, Map.class);
            //dataSourceProps: key -> 配置名  value : 配置值
            dataSourceMap.put(dbName, dataSourceProps);
        }

        // 默认数据源
        String defaultData = environment.getProperty(prefix + "default");
        defaultDataSourceConfig = PropertyUtil.handle(environment, prefix + defaultData, Map.class);

    }
}
