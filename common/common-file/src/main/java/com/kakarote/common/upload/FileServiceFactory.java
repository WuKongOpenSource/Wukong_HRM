package com.kakarote.common.upload;

import com.kakarote.common.upload.entity.UploadProperties;
import com.kakarote.common.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * 文件服务构建工厂
 *
 * @author zhangzhiwei
 */
@Configuration
@Slf4j
public class FileServiceFactory {

    @Bean
    @ConditionalOnMissingBean
    public static FileService fileService(UploadProperties properties) {
        if (properties.getType() == null) {
            throw new IllegalArgumentException("type is not null");
        }
        final Iterator<FileService> iterator = ServiceLoader.load(FileService.class).iterator();
        while (iterator.hasNext()) {
            try {
                FileService service = iterator.next();
                if (properties.getType().equals(service.getType())) {
                    return service.build(properties);
                }
            } catch (ServiceConfigurationError ignored) {

            }
        }
        log.warn("The spi cannot load the configuration,type:{}", properties.getType());
        throw new IllegalArgumentException("The spi cannot load the configuration");
    }
}
