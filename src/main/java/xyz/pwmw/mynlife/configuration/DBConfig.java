//package xyz.pwmw.mynlife.configuration;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.hibernate.cfg.AvailableSettings;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.hibernate5.SpringBeanContainer;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import xyz.pwmw.mynlife.util.ApplicationYamlRead;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "entityManagerFactory",
//        transactionManagerRef = "entityTransactionManager",
//        basePackages = "xyz.pwmw.mynlife.model"
//)
//public class DBConfig {
//    @Autowired
//    private ApplicationYamlRead applicationYamlRead;
//
//    @Bean
//    public DataSource mysqlDataSource() {
//        HikariDataSource hikariDataSource = new HikariDataSource();
////        hikariDataSource.addDataSourceProperty("useSSL", true);
////        hikariDataSource.addDataSourceProperty("requireSSL", true);
////        hikariDataSource.addDataSourceProperty("verifyServerCertificate", true);
//        hikariDataSource.setDriverClassName(applicationYamlRead.getDriver_class_name());
//        hikariDataSource.setJdbcUrl(applicationYamlRead.getUrl());
//        hikariDataSource.setUsername(applicationYamlRead.getUsername());
//        hikariDataSource.setPassword(applicationYamlRead.getPassword());
////        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        System.out.println("잘되나 test");
////        dataSource.setDriverClassName(env.getProperty("spring.donda.datasource.driver-class-name"));
////        dataSource.setUrl(env.getProperty("spring.donda.datasource.url"));
////        dataSource.setUsername(env.getProperty("spring.donda.datasource.username"));
////        dataSource.setPassword(env.getProperty("spring.donda.datasource.password"));
//        return hikariDataSource;
//    }
//
//
//    @Primary
//    @Bean(name = "entityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource, ConfigurableListableBeanFactory beanFactory) {
//        Map<String, Object> properties = new HashMap<String, Object>();
//        // yml이나 properties에서도 써줄 수 있지만 여러 디비를 관리하다보면 밑에와같이 쓸 수 있습니다.
//        // properties.put("hibernate.hbm2ddl.auto", "update");
//        // properties.put("hibernate.hbm2ddl.auto", "none");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//        properties.put("show-sql", "true");
//        LocalContainerEntityManagerFactoryBean build = builder
//                .dataSource(mysqlDataSource())
////                .dataSource(dataSource)
//                .properties(properties)
//                //.packages(TestModel.class)
//                .packages("xyz.pwmw.mynlife.model")
////                .persistenceUnit("userPU")
//                .build();
//
//        build.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
//        return build;
//    }
//
//
//    @Bean(name = "entityTransactionManager")
//    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
//    private Map hibernateProperties() {
//        Resource resource = new ClassPathResource("hibernate.properties");
//
//        try {
//            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
//
//            return properties.entrySet().stream()
//                    .collect(Collectors.toMap(
//                            e -> e.getKey().toString(),
//                            e -> e.getValue())
//                    );
//        } catch (IOException e) {
//            return new HashMap();
//        }
//    }
//}