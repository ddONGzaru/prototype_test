package test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015-11-06.
 */
@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public String strBean() {

        return "test";
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ReporterExecutor-");

        executor.initialize();

        return executor;
    }

    @Bean
    public ThrottledTaskExecutor throttledTaskExecutor() {

        ThrottledTaskExecutor executor = new ThrottledTaskExecutor(taskExecutor(), 4);

        return executor;
    }

    public Executor taskExecutorForRegion() {

        return Executors.newSingleThreadExecutor();
    }

}
