package com.github.jjjzzzqqq.mini.db.router;

import com.github.jjjzzzqqq.mini.db.router.annotation.DBRouter;
import com.github.jjjzzzqqq.mini.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分库分表切面处理类<br>
 * 通过该类，处理路由策略，将该次请求的路由信息保存到上下文中<br>
 * 此处无需注入，中间件的所有类都不应该依赖于Spring的任何注解，降低与Spring的耦合<br>
 * 通过AutoConfig一次性注入所有需要的Bean
 */
@Aspect
public class DBRouterAspect {

    private Logger logger = LoggerFactory.getLogger(DBRouterAspect.class);

    /**
     * 不使用依赖注入,使用最基础的构造器注入
     */
    private DBRouterConfig dbRouterConfig;

    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterAspect(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }


    /**
     * 切点：所有包含DBRouter的方法
     */
    @Pointcut("@annotation(com.github.jjjzzzqqq.mini.db.router.annotation.DBRouter)")
    public void aopPoint() {
    }



    @Around("aopPoint() && @annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {
        String dbKey = StringUtils.isNotBlank(dbRouter.key()) ? dbRouter.key() : dbRouterConfig.getRouterKey();
        //如果注解的dbKey属性为空且配置文件未配置
        if (StringUtils.isBlank(dbKey)) {
            throw new RuntimeException("annotation DBRouter key is null！");
        }

        //从方法参数中获取dbKey对应的路由属性
        String dbKeyAttr = getAttrValue(dbKey, jp.getArgs());

        //执行路由策略算法
        dbRouterStrategy.doRouter(dbKeyAttr);

        //返回结果
        try {
            return jp.proceed();
        } finally {
            dbRouterStrategy.clear();
        }

    }

    public String getAttrValue(String attr, Object [] args) {
        String filedValue = null;
        for (Object arg : args) {
            try {
                //如果已经找到了dbKey属性,直接退出
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                filedValue = BeanUtils.getProperty(arg, attr);
            } catch (Exception e) {
                logger.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }

        return filedValue;
    }
}
