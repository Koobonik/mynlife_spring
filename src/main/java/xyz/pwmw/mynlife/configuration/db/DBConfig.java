package xyz.pwmw.mynlife.configuration.db;


import com.spring.util.ApplicationYamlRead;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "tobecchachaEntityManager",
        transactionManagerRef = "tobecchachaTransactionManager",
        basePackages = "com.spring.model"
)
public class DBConfig {
    @Autowired
    private ApplicationYamlRead applicationYamlRead;

    @Bean
    public DataSource mysqlDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.addDataSourceProperty("useSSL", true);
//        hikariDataSource.addDataSourceProperty("requireSSL", true);
//        hikariDataSource.addDataSourceProperty("verifyServerCertificate", true);
        hikariDataSource.setDriverClassName(applicationYamlRead.getDriver_class_name());
        hikariDataSource.setJdbcUrl(applicationYamlRead.getUrl());
        hikariDataSource.setUsername(applicationYamlRead.getUsername());
        hikariDataSource.setPassword(applicationYamlRead.getPassword());
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        System.out.println("잘되나 test");
//        dataSource.setDriverClassName(env.getProperty("spring.donda.datasource.driver-class-name"));
//        dataSource.setUrl(env.getProperty("spring.donda.datasource.url"));
//        dataSource.setUsername(env.getProperty("spring.donda.datasource.username"));
//        dataSource.setPassword(env.getProperty("spring.donda.datasource.password"));
        return hikariDataSource;
    }


    @Bean(name = "tobecchachaEntityManager")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = new HashMap<String, Object>();
        // yml이나 properties에서도 써줄 수 있지만 여러 디비를 관리하다보면 밑에와같이 쓸 수 있습니다.
        // properties.put("hibernate.hbm2ddl.auto", "update");
        // properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("show-sql", "true");
        return builder
                .dataSource(mysqlDataSource())
                .properties(properties)
                //.packages(TestModel.class)
                .packages("com.spring.model")
                .persistenceUnit("userPU")
                .build();
    }


    @Bean(name = "tobecchachaTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("tobecchachaEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map hibernateProperties() {
        Resource resource = new ClassPathResource("hibernate.properties");

        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);

            return properties.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue())
                    );
        } catch (IOException e) {
            return new HashMap();
        }
    }
}