# db-router-spring-boot-starter

## 概述

轻量级的数据库分库分表中间件，支持功能有

1. 根据Key进行Hash映射，实现分库、分表（可选）
2. 通过注解实现数据源切换，分库对业务代码零侵入
3. 通过MyBatis拦截器重写SQL，分表对业务代码零侵入
4. 整合SpringBoot，支持零代码编写开箱即用

待完善功能

1. 暂不支持复杂SQL查询
2. 暂不支持多表事务
3. 暂不支持集中式分布式ID生成，需要使用者自己生成
4. 多种路由算法支持，如范围映射

## 实现思路



AOP拦截

数据源切换

路由算法

SQL重写

开箱即用