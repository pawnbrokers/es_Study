package com.yuan.jd_es;

import com.yuan.jd_es.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class JdEsApplicationTests {

    @Autowired
    ContentService contentService;

    @Test
    void contextLoads() throws IOException {

        Boolean java = contentService.parseContent("java");
        System.out.println(java);
    }

}
