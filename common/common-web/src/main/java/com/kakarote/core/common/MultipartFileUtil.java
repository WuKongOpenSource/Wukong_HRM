package com.kakarote.core.common;

import cn.hutool.core.io.FileUtil;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件转换为MultipartFile工具类
 *
 * @author zhangzhiwei
 */
public class MultipartFileUtil {


    /**
     * 文件转换为MultipartFile
     *
     * @param file 文件对象
     * @return MultipartFile
     */
    public static MultipartFile getMultipartFile(File file) {
        return new MockMultipartFile(file.getName(), file.getName(), "application/octet-stream", FileUtil.readBytes(file));
    }

    /**
     * 模拟的MultipartFile，用于服务间的文件上传
     */
    public static class MockMultipartFile implements MultipartFile {

        private final String name;

        private final String originalFilename;

        private final String contentType;

        private final byte[] content;

        /**
         * Create a new MockMultipartFile with the given content.
         *
         * @param name             the name of the file
         * @param originalFilename the original filename (as on the client's machine)
         * @param contentType      the content type (if known)
         * @param bytes            the content of the file
         */
        public MockMultipartFile(String name, String originalFilename, String contentType, byte[] bytes) {
            Assert.hasLength(name, "Name must not be null");
            this.name = name;
            this.originalFilename = (originalFilename != null ? originalFilename : "");
            this.contentType = contentType;
            if (bytes == null) {
                bytes = new byte[0];
            }
            this.content = bytes;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return (this.content.length == 0);
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return this.content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.content, dest);
        }

    }
}
