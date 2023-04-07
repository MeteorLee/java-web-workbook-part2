package org.zerock.b01.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
import static org.modelmapper.convention.MatchingStrategies.LOOSE;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

@Configuration
public class RootConfig {

    // modelMapper 설정
    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE)
                // 느슨한 관리 - 특정 객체의 내부까지 적용
                .setMatchingStrategy(LOOSE);
                // 강력한 관리 - FetchType.LAZY 상황에서 반영 안됨
//                .setMatchingStrategy(STRICT);



        return modelMapper;
    }
}
