package com.yuan.jd_es.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yuan.jd_es.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {
    @Autowired
    ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keywords) throws IOException {
        return contentService.parseContent(keywords);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchPage(@PathVariable("keyword") String keyword,
                                                @PathVariable("pageNo") int pageNo,
                                                @PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.searchHighLightPage(keyword, pageNo, pageSize);
    }

    @GetMapping("/search1/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchHighLightPage(@PathVariable("keyword") String keyword,
                                                         @PathVariable("pageNo") int pageNo,
                                                         @PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.searchHighLightPage(keyword, pageNo, pageSize);
    }

}
