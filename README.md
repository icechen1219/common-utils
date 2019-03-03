# 通用工具包
### 将项目中经常用到的一些通用工具类剥离出来，单独成为一个工程
使用说明：
- maven
克隆工程到本地，然后运行mvn install，最后复制如下依赖到需要使用该工具包的maven工程中
```
        <dependency>
            <groupId>cn.weyoung</groupId>
            <artifactId>common-utils</artifactId>
            <version>1.0</version>
        </dependency>
```

- jar
```
下载dist目录里面已经编译好的jar包，放到你的classpath中
注意需要引入pom.xml中对应的依赖库
```
