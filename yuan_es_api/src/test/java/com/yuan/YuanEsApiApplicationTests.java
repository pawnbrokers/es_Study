package com.yuan;

import com.alibaba.fastjson.JSON;
import com.yuan.pojo.User;
import org.apache.lucene.search.suggest.analyzing.FSTUtil;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * es7.6.x高级客户端测试api
 */
@SpringBootTest
class YuanEsApiApplicationTests {


    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //测试索引的创建 Request  PUT kaung_index
    @Test
    void testCreatedIndex() throws IOException {
        //1. 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("kuang_index");

        // 2. 客户端执行创建请求
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
    }


    //测试获取索引,只能判断其是否存在
    @Test
    void testExistIndex() throws IOException {
        // 1. 获得索引请求
        GetIndexRequest request = new GetIndexRequest("kuang_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        // 1. 获得索引请求
        DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //1. 创建对象
        User user = new User("狂神说", 3);
        //2. 创建请求
        IndexRequest request = new IndexRequest("kuang_index");
        //3. 规则 put /kuang_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //4.将我们的请求放入请求，先引入fastjson
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);

        //5. 客户端发送请求
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);

        //6. 获取响应结果
        System.out.println(index.toString());
        System.out.println(index.status());//对应我们命令返回的状态

    }


    //获取文档，判断是否存在
    @Test
    void testIsExist() throws IOException {
        GetRequest request = new GetRequest("kuang_index", "1");
        request.fetchSourceContext(new FetchSourceContext(false));//不获取返回的_source的上下文
        request.storedFields("_none_");

        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    //获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("kuang_index", "1");
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString());//打印文档内容
        System.out.println(documentFields);
    }


    //更新文档信息
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("kuang_index", "1");
        User user = new User("狂神说Java", 99);
        request.timeout("1s");
        UpdateRequest doc = request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update);//

    }


    //删除文档记录
    @Test
    void testDEleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("kuang_index", "1");
        request.timeout("1s");
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }


    //特殊的项目，一般会批量插入数据
    @Test
    void TestBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("yuan1", 43));
        userList.add(new User("yuan2", 43));
        userList.add(new User("yuan3", 43));
        userList.add(new User("yuan4", 43));
        userList.add(new User("yuan5", 43));
        userList.add(new User("yuan6", 43));

        //批处理请求
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(new IndexRequest("kuang_index")
                    .id("" + (i + 1))
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());//是否失败，false带便成功
    }


    //测试查询
    @Test
    void testSearch() throws IOException {
        //1. 建立查询请求
        SearchRequest searchRequest = new SearchRequest("kuang_index");

        // 2. 建立SearchSourceBuilder来设置具体参数
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 3. 设置具体的查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "kuangshen");
        searchSourceBuilder.query(termQueryBuilder);
        SearchSourceBuilder timeout = searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest source = searchRequest.source(searchSourceBuilder);

        //4. 交由客户端执行请求
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

        // 5. 处理查询结果
        SearchHits hits = search.getHits();
        String string = JSON.toJSONString(hits);
        System.out.println("--------------------------");
        for (SearchHit hit : hits.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }


}
