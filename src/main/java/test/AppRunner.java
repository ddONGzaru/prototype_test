package test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import test.config.ThrottledTaskExecutor;

/**
 * Created by Administrator on 2015-11-06.
 */
@SpringBootApplication
public class AppRunner {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(AppRunner.class);

        ThrottledTaskExecutor executor = context.getBean("throttledTaskExecutor", ThrottledTaskExecutor.class);

        for (int i = 0; i < 10; i++) {

            Runnable runnable = context.getBean("publisher", Runnable.class);
            executor.execute(runnable);
        }

    }
}
