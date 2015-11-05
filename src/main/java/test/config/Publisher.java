package test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015-10-03.
 */
@Scope("prototype")
@Component
public class Publisher implements Runnable {

    @Autowired
    TestBean bean;


    public void run() {
        System.out.println(this + ":: " + System.currentTimeMillis());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
