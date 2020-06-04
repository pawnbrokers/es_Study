package com.yuan.jd_es.utils;

import com.yuan.jd_es.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("爱情").forEach(System.out::println);
    }

    public List<Content> parseJD(String keyword) throws IOException {
        //获取请求 https://search.jd.com/Search?keyword=java
        //前提，联网，不能获取ajax
        String url = "https://search.jd.com/Search?keyword=" + keyword + "&enc=utf-8";

        //解析网页(返回的document就是js页面对象，就是浏览器的document对象),所有js可以使用的方法这里都可以用
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());

        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> goodsList = new ArrayList<>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

           /* System.out.println("+++++++++++++++++++++++++++");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);*/

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);
        }
        return goodsList;
    }

}
