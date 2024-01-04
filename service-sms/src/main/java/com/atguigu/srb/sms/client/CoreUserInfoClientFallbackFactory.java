package com.atguigu.srb.sms.client;


import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class CoreUserInfoClientFallbackFactory implements FallbackFactory<CoreUserInfoClient> {


    @Override
    public CoreUserInfoClient create(Throwable cause) {
        return new CoreUserInfoClient() {
            @Override
            public boolean checkMobile(String mobile) {
                log.debug("===========================================================验证手机号接口熔断{}",cause.toString(),cause);
                return false;
            }
        };
    }
}
