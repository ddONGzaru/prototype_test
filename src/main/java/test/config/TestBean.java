package test.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2015-11-06.
 */
@Component
@Scope("prototype")
public class TestBean {

    private String testStr = "str";

    public String getTestStr() {
        return testStr;
    }
}
