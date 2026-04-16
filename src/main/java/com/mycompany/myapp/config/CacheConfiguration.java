package com.mycompany.myapp.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration //đánh dấu 1 file cấu hình
@EnableCaching // khởi dộng cache cho toàn bộ dự án
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>(); // khởi tạo 1 đối tượng cấu hình cache cơ bản cảu Java(JCache)
        // lấy địa chỉ redis từ các file application.yml vd redis://localhost:6379
        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        Config config = new Config(); // thiết lập cách đóng gói dữ liệu từ java để sang redis
        // Fix Hibernate lazy initialization https://github.com/jhipster/generator-jhipster/issues/22889
        config.setCodec(new org.redisson.codec.SerializationCodec()); //dùng SerializationCodec để giải quyết lỗi lazy initialization
        if (jHipsterProperties.getCache().getRedis().isCluster()) { //này nếu redis chạy theo cụm(nhiều máy chủ redis)
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else { //mình đang dùng singleRedis nên nó nhảy vào đây
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize()) // set kết nối tối đa
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]); //set địa chỉ redis

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        ); //thiết lập thời gian sống của dữ liệu trong cache
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    } //nói với Hibernate rằng khi dùng đến tầng cache thứ 2 hãy dùng CACHE_MANAGER cấu hình redis

    @Bean //tạo danh sách vùng nhớ sau muốn tìm gì vào vùng nhớ tương ứng mà tìm
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.mycompany.myapp.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Category.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Author.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Book.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.InventoryReceipt.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.ReceiptDetail.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Customer.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Order.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.OrderDetail.class.getName(), jcacheConfiguration);
            createCache(cm, com.mycompany.myapp.domain.Employee.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        // hàm này kiểm tra vùng nhớ (cache name) tồn tại trên redis chưa
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) { // tồn tại thì xóa dữ liệu cũ đảm bảo dữ liệu mới nhất
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean //tạo ra key từ 2 thuộc tính đảm bảo rằng các phiên bản khác nhau của code luôn chạy dữ liệu redis mới nhất không được gọi lại cái redis lưu dữ liệu thực thể phiên bản code cũ
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
