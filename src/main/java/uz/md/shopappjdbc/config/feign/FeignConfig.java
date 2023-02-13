package uz.md.shopappjdbc.config.feign;

import feign.Contract;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public CustomFeignRequestLogging customFeignRequestLogging() {
        return new CustomFeignRequestLogging();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

}