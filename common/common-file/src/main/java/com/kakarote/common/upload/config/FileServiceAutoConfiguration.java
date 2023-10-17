package com.kakarote.common.upload.config;

import com.kakarote.common.upload.FileServiceFactory;
import com.kakarote.common.upload.entity.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * spring自动注入fileService
 *
 * @author zhangzhiwei
 */
@Configuration
@EnableConfigurationProperties(UploadProperties.class)
@Import({FileServiceFactory.class})
public class FileServiceAutoConfiguration {
}
