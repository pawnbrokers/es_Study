![image-20200604140334918](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190540.png)

主要参考来源：https://www.bilibili.com/video/BV17a4y1x7zq?p=1

# 1. 什么是Elastic Search

<!-- more -->

## 1.1 ElasticSearch的特点

+ Elaticsearch，简称为es， es是一个开源的高扩展的**分布式全文检索引擎**，它可以近乎实时的存储、检索数据；

+ 本身扩展性很好，可以扩展到上百台服务器，**处理PB级别（大数据时代）的数据**。
+ **es也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能**，但是它的目的是**通过简单的RESTfulAPI**来隐藏Lucene的复杂性，从而让全文搜索变得简单。  



## 1.2 Lucene

Lucene的**目的是为软件开发人员提供一个简单易用的工具包**，以方便的在目标系统中实现全文检索的功能，或者是以此为基础建立起完整的全文检索引擎。

Lucene是一套用于全文检索和搜寻的开源程式库，由Apache软件基金会支持和提供。

Lucene提供了一个简单却强大的应用程式接口，能够做全文索引和搜寻。**在Java开发环境里Lucene是一个成熟的免费开源工具。就其本身而言，Lucene是当前以及最近几年最受欢迎的免费Java信息检索程序库。**  



# 2. ElasticSearch安装

**ElasticSearch要保证和我们的Java核心jar包版本对应。**

## 2.1 目录查看

![image-20200604140823533](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190541.png)

```shell
bin  #启动文件
config #配置文件
log4j2 #日志配置文件
jvm.options java #虚拟机相关的配置
elasticsearch.yml #elasticsearch 的配置文件！ 默认 9200 端口！ 跨域！
lib #相关jar包
logs #日志！
modules #功能模块
plugins #插件！
```



## 2.2 启动访问

运行`bin`目录下的`elasticsearch.bat`，默认端口为9200

![image-20200604141040191](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190542.png)

可以发现`elasticsearch`自成一个集群。

我想我们暂时可以把`elasticsearch`当作一种数据库来看待。

## 2.3 安装可视化界面es header（类比sqlyog）

### 2.3.1 启动

```bash
npm install
npm run start
```



### 2.3.2 配置es解决跨域问题

```yml
http.cors.enabled: true
http.cors.allow-origin: "*"
```



### 2.3.3 重启es服务器，然后再次连接

![image-20200604142018799](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190543.png)



## 2.4 Kibana

Kibana是一个针对**Elasticsearch的开源分析及可视化平台**，用来搜索、查看交互存储在Elasticsearch索引中的数据。使用Kibana，可以通过各种图表进行高级数

据分析及展示。Kibana让海量数据更容易理解。它操作简单，基于浏览器的用户界面可以快速创建仪表板（dashboard）实时显示Elasticsearch查询动态。设置

Kibana非常简单。无需编码或者额外的基础架构，几分钟内就可以完成Kibana安装并启动Elasticsearch索引监测。  



### 2.4.1 访问测试(默认端口：5601)

![image-20200604142218788](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190544.png)





### 2.4.2 我们可以使用开发者工具进行调试

![image-20200604142306019](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190545.png)



# 3. ES核心概念

## 3.1 总览

+ 一切都是JSON

+ 索引
+ 字段类型（mapping）
+ 文档（document）

> ElasticSearch是面向文档的，关系行数据库和ElasticSearch做一个对比

|    Ralational DB     |  ElasticSearch  |
| :------------------: | :-------------: |
| 数据库（datasource） | 索引（indices） |
|     表（table）      |      types      |
|      行（rows）      |    documents    |
|   字段（columns）    |     fields      |

ElasticSearch（集群）中可以包含多个索引（数据库），每个索引中可以包含多个类型（表），每个类型下又包含多个文档（行），每个文档中又包含多个字段（列）。

**物理设计：**

Elastic Search在后台把每个索引分成多个分片，每个分片可以在集群中的不同服务器之间迁移，但是它一个人也是一个集群。



**逻辑设计：**

一个索引类型中，包含有多个文档，比如文档1、文档2，当我们索引一篇文章的时候，可以通过这样的顺序找到他：

**索引 > 类型 > 文档ID**

**注意：ID不必是一个整数，实际上他是一个字符串。**



## 3.2 文档

**文档就是我们的一条条数据**

```sql
user
1	yuantangbo	18
2	zhangsan	3
```

ElasticSearch是面向文档的，**也就是说索引和搜索数据的最小单位是文档。**

文档有一些重要属性：

+ **自我包含**，一篇文档同时包含字段和对应的值，也就是同时包含 key:value！  
+ **可以是层次性的**，一个文档中包含自文档，复杂的逻辑实体就是这么来的！ {就是一个json对象！fastjson进行自动转换！}  

+ **灵活的结构**，文章不依赖于预先定义的模式，我们知道关系型数据库中，要提前定义字段才能使用，在elasticsearch中，对于字段是非常灵活的，有时候，我

  们可以忽略该字段，或者动态的添加一个新的字段。  



## 3.3 索引

### 3.3.1 什么是索引

索引可以类比数据库。

**索引是映射类型的容器**，elasticsearch中的索引是一个非常大的文档集合。索引存储了映射类型的字段和其他设置。 然后它们被存储到了各个分片上了。 我们来

研究下分片是如何工作的。  

![image-20200604144546172](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190546.png)



### 3.3.2 **分片是什么？**

简单来讲就是咱们在ES中所有数据的文件块，也是**数据的最小单元块**，整个ES集群的核心就是对所有分片的分布、索引、负载、路由等达到惊人的速度

> 实列场景：
>
> 假设 IndexA 有2个分片，我们向 IndexA 中插入10条数据 (10个文档)，那么这10条数据会尽可能平均的分为5条存储在第一个分片，剩下的5条会存储在另一个分片中。

![image-20200604145055137](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190547.png)

如图，在集群1中有两个节点，并使用了默认的分片配置. ES自动把这5个主分片分配到2个节点上, 而它们分别对应的副本则在完全不同的节点上。其中 node1 有

某个索引的分片1、2、3和副本分片4、5，node2 有该索引的分片4、5和副本分片1、2、3。



实际上，**一个分片是一个Lucene索引，一个包含倒排索引的文件目录**，**倒排索引**的结构使 得elasticsearch在不扫描全部文档的情况下，就能告诉你哪些文档包含

特定的关键字。  



### 3.3.3 何为倒排索引

一个未经处理的数据库中，**一般是以文档ID作为索引，以文档内容作为记录。**

而倒排索引指的是**将单词或记录作为索引，将文档ID作为记录**，这样便可以方便地通过单词或记录查找到其所在的文档。

![image-20200604145649823](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190548.png)

创建倒排索引，分为以下几步：

1）创建文档列表：

l lucene首先对原始文档数据进行编号（DocID），形成列表，就是一个文档列表

![](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190549.png)

2）创建倒排索引列表

l 然后对文档中数据进行分词，得到词条。对词条进行编号，以词条创建索引。然后记录下包含该词条的所有文档编号（及其它信息）。

![img](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190550.png)

谷歌之父--> 谷歌、之父



**倒排索引创建索引的流程：**

1） 首先把所有的原始数据进行编号，形成文档列表

2） 把文档数据进行分词，得到很多的词条，以词条为索引。保存包含这些词条的文档的编号信息。



**搜索的过程：**

当用户输入任意的词条时，首先对用户输入的数据进行分词，得到用户要搜索的所有词条，然后拿着这些词条去倒排索引列表中进行匹配。找到这些词条就能找到包含这些词条的所有文档的编号。

然后根据这些编号去文档列表中找到文档



比如我们通过博客标签来搜索博客文章，那么倒排索引列表就是这样的一个结构：

![image-20200604145903191](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190551.png)

如果要搜索含有 python 标签的文章，那相对于查找所有原始数据而言，查找倒排索引后的数据将会快的多。只需要 查看标签这一栏，然后获取相关的文章ID即

可。**完全过滤掉无关的所有数据，提高效率！**  



# 4. IK分词器插件

![image-20200604150004192](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190552.png)

## 4.1 什么是分词器

**分词：**即**把一段中文或者别的划分成一个个的关键字**，我们在搜索时候会把自己的信息进行分词，会把数据库中或者索引库中的数据进行分词，然后进行一个匹配

操作，默认的中文分词是将每个字看成一个词，比如 “科比布莱恩特 会被分为"科","比","布","莱"，”恩“，”特“，这显然是不符合要求的，所以我们需要安装中文分词

器ik来解决这个问题。

如果要使用中文，建议使用ik分词器！IK提供了两个分词算法**：ik_smart 和 ik_max_word，其中 ik_smart 为最少切分，ik_max_word为最细粒度划分！**  

## 4.2 使用方法

下载地址：https://github.com/medcl/elasticsearch-analysis-ik  

下载后解压放在ES的plugins文件夹下就可以了，重启ES，发现分词器被自动加载



## 4.3 使用Kibana测试

**ik_smart:**

![image-20200604150434913](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190553.png)



**ik_max_word:**

**它按照字典穷尽了所有的可能。**

![image-20200604150527548](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190554.png)



## 4.4 测试 我叫李相赫

![image-20200604150734097](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190555.png)

发现哪怕我们使用了`ik_smar`t却还是把我们的`李相赫`给**分词**了。



## 4.5 手动添加字典

步骤：

1. 在对应的`ik插件文件夹`的`config文件夹`下新增我们的字典，并加入相应字段
2. 在对应的xml文件中加入我们自定义的字典
3. 重启ES

![image-20200604151107179](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190556.png)

![image-20200604151114831](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190557.png)

重启测试：

![image-20200604151811060](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190558.png)

![image-20200604151818290](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190559.png)

**发现李相赫三个字不再会被分词器拆分。**



# 5. REST风格说明

| method | url地址                                         | 描述                   |
| ------ | ----------------------------------------------- | ---------------------- |
| PUT    | localhost:9200/索引名称/类型名称/文档id         | 创建文档（指定文档id） |
| POST   | localhost:9200/索引名称/类型名称                | 创建文档（随机文档id） |
| POST   | localhost:9200/索引名称/类型名称/文档id/_update | 修改文档               |
| DELETE | localhost:9200/索引名称/类型名称/文档id         | 删除文档               |
| GET    | localhost:9200/索引名称/类型名称/文档id         | 查询文档通过文档id     |
| POST   | localhost:9200/索引名称/类型名称/_search        | 查询所有数据           |



# 6. 关于索引的基本操作

## 6.1 创建索引

```
PUT /索引名/~类型名~/文档id
{请求体}
```

![image-20200604152359076](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190600.png)

去es-head中看看

![image-20200604152441364](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190601.png)

发现对应的索引、类型、文档都已被创建。



## 6.2 创建规则

上述方式插入的时候采用的是默认的数据类型，**我们当然可以在创建索引的时候就事先指定字段的数据类型。**

![image-20200604152750653](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190602.png)

![image-20200604152831074](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190603.png)

## 6.3 查看信息 GET 

当然我们也可以通过`GET /索引名`来获得具体的信息

![image-20200604152937144](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190604.png)



**如果我们没有指定字段数据类型，es会问我们自动指定，我们可以来看一下test1索引的默认书库类型是什么？**

![image-20200604153126146](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190605.png)



## 6.4 更新文档信息

### 6.4.1 方法1 PUT

```
PUT /索引名/类型/文档 id
{请求体}
```

发现版本号变成了2，而且状态返回时`"update"`而非`"created"`。

![image-20200604153324852](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190606.png)



### 6.4.2 方法2（推荐） post+ _update

```
POST /索引名/类型/文档id/update
{
"doc":{
	"要修改的字段名"："新值"
}
```

![image-20200604153724017](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190607.png)

同样是版本号变成了3，并且返回状态是`updated`.

### 6.4.3 比较

+ 默认的PUT方法必须指定全部字段，否则不赋值的字段会直接为空
+ POST + _update的方法，只需要修改我们需要修改的字段即可，其他字段保持原始值不变





# 7. 关于文档的操作

## 7.1 基本操作

添加数据、修改数据、删除数据，暂时都不再讲

### 7.1.1 查询数据

```
GET /索引名/类型/文档id
```

![image-20200604154105764](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190608.png)



### 7.1.2 简单地条件搜索 _search

![image-20200604154214682](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190609.png)



## 7.2 复杂操作

> **排序、分页、高亮、模糊查询、精准查询**

### 7.2.1 查询结构体样式

![image-20200604154525749](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190610.png)

我们首先es中有两条数据

```html
GET test1/user/_search
{
  "query":{
    "match": {
      "name": "张三"
    }
  }
}
```

![image-20200604154633439](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190611.png)

发现的东西：

+ 查询出来的结果都封装在名为`hits`的对象中

+ 可以通过`hits`获得`total`和`hits`
+ 查询结果默认按照`score`进行排序



### 7.2.2 过滤搜索结果 _source

```
GET 索引/类型/_search
{
	"query":{
		搜索请求体
	},
	"_source": [筛选的字段（逗号隔开）]
}
```

![image-20200604155127714](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190612.png)

这里我们发现就会只搜索出名字，而年龄并未封装出来。



### 7.2.3 排序

指定排序，和排序的字段以及规则

```
GET test1/user/_search
{
  "query":{
    "match": {
      "name": "张三"
    }
  },
  "_source": ["name"],
  "sort": [
    {
      "age": {
        "order": "desc"
      }
    }
  ]
}
```

这里我们按照年龄逆序排列

![image-20200604155345635](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190613.png)



### 7.2.4 分页查询

```
GET test1/user/_search
{
  "query":{
    "match": {
      "name": "张三"
    }
  },
  "from": 0,
  "size": 1
}
```

这里我们查询出了多条记录，但是只显示1条

![image-20200604155557836](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190614.png)



### 7.2.5 布尔值查询

![image-20200604155710790](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190615.png)

```GET test1/user/_search
GET test1/user/_search
{
  "query": {
    "bool": {
      "must": [
        {"match": {
          "name": "张三"
        }},
        {
          "match": {
            "age": "24"
          }
        }
      ]
    }
  }
}
```

这样就查询出名字中含有“张三”并且年龄等于24的文档了。

![image-20200604160023944](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190616.png)



### 7.2.6 精准匹配 term

term 查询是直接通过倒排索引指定的词条进程精确查找的！

**关于分词：**

**term ，直接查询精确的**

**match，会使用分词器解析**！（先分析文档，然后在通过分析的文档进行查询！）  

![image-20200604160708354](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190617.png)



### 7.2.7 高亮查询

`highlights`

**前缀后缀+对应字段**

```
GET test1/_search
{
  "query": {
    "match": {
      "name": "张三"
    }
  },
  "highlight": {
    "pre_tags": "<span style='color:red'>",
    "post_tags": "</span>",
    "fields": {
      "name": {}
    }
  }
}
```



![image-20200604160942884](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190618.png)





# 8. 集成SpringBoot

我们这里选择`Java High Level REST Client`

![image-20200604162110112](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190619.png)



## 8.1 导入依赖

官方文档指导 的依赖为：

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.7.1</version>
</dependency>
```

![image-20200604162305971](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190620.png)

我们在springboot项目中导入

```xml
 <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

就默认添加了上述两个依赖。



## 8.2 创建客户端

![image-20200604162532550](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190621.png)

这里我们新建一个config文件，@Bean注入对应的客户端。

```java
@Configuration
public class ElasticSearchConfig {

    @Bean
    RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http")
                )
        );
    }
}
```



## 8.3 编写请求

![image-20200604162950080](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190622.png)

## 8.4 对应的API

![image-20200604163019841](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190623.png)



## 8.5 封装实体类

```java
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private int age;
}
```



## 8.6 API测试

### 8.6.1 创建索引

对应的是`CreateRequest`

![image-20200604163458692](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190624.png)

```java
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
```



### 8.6.2 测试索引是否存在

```java
//测试获取索引,只能判断其是否存在
@Test
void testExistIndex() throws IOException {
    // 1. 获得索引请求
    GetIndexRequest request = new GetIndexRequest("kuang_index");
    boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
    System.out.println(exists);
}
```

![image-20200604163442067](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190625.png)



### 8.6.3 测试删除索引

```java
  //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        // 1. 获得索引请求
        DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }
```



![image-20200604163416473](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190626.png)



### 8.6.4 测试添加文档

创建文档使用的是`IndexRequest`

```java
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
```



### 8.6.5 测试文档是否存在

```java
//获取文档，判断是否存在
@Test
void testIsExist() throws IOException {
    GetRequest request = new GetRequest("kuang_index", "1");
    request.fetchSourceContext(new FetchSourceContext(false));//不获取返回的_source的上下文
    request.storedFields("_none_");

    boolean exists = client.exists(request, RequestOptions.DEFAULT);
    System.out.println(exists);
}
```

![image-20200604164009435](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190627.png)



### 8.6.6 获取文档信息GetRequest

```java
    //获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("kuang_index", "1");
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString());//打印文档内容
        System.out.println(documentFields);
    }
```

![image-20200604164104455](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190628.png)



### 8.6.7 更新文档信息 UpdateRequest

这里采用的使我们之前使用的第二种方法

```java
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
```

![image-20200604164157716](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190629.png)



### 8.6.8 测试删除文档信息 DeleteRequest

```java
//删除文档记录
@Test
void testDEleteDocument() throws IOException {
    DeleteRequest request = new DeleteRequest("kuang_index", "1");
    request.timeout("1s");
    DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
    System.out.println(delete.status());
}
```

![image-20200604164314263](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190630.png)



### 8.6.9 批量插入文档 BulkRequest

```java
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
```

![image-20200604164437997](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190631.png)



### 8.6.10 测试查询多条数据 SearchRequest

```java
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
```

![image-20200604164650663](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190632.png)



`SearchRequest`复杂查询

![image-20200604164918836](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190633.png)

1. 首先创建请求，交由客户端来执行
2. SearchSourceBuilder是用来设置查询参数的
3. 使用searchSourceBuilder来设置具体的查询参数
4. 真个builder作为请求体交由request处理
5. 客户端执行request

![image-20200604165346550](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190634.png)



# 9. 实战 （仿京东搜索）

## 9.1 最终效果

![image-20200604165511503](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190635.png)



## 9.2 需要的依赖（页面以及对应的js）

![image-20200604165557574](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190636.png)



## 9.3 数据获取（Jsoup爬虫）

### 9.3.1 实体类

我们暂时只爬取书名、图片、价格

```java
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String title;
    private String img;
    private String price;
}
```



### 9.3.2 爬虫工具类

```java
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
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
```



### 9.3.3 前端资源

信息的主要来源id为`J_goodList`

![image-20200604165926879](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190637.png)



图片对应`img`下的`src`。

![image-20200604170014343](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190638.png)



书名对应`class`为`p_name`。

![image-20200604170119983](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190639.png)

价格对应class为`p_price`。

![image-20200604170203738](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190640.png)



### 9.3.4 再看代码

![image-20200604170508061](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190641.png)



### 9.3.5 获取资源的Service层

```java
@Service
public class ContentService {


    @Autowired
    RestHighLevelClient restHighLevelClient;

    //1.解析数据放入es索引库中
    public Boolean parseContent(String keywords) throws IOException {
        List<Content> contents = new HtmlParseUtil().parseJD(keywords);

        //2. 把查询的数据放入es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
```

![image-20200604185752829](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190642.jpg)

## 9.4 获取资源的Controller

![image-20200604185833509](https://yuancc.oss-cn-hangzhou.aliyuncs.com/typora/20200604190643.jpg)



## 9.5 展示的Controller 和Service

```java
//2. 获取这些数据，实现搜索功能
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }


    //实现搜索高亮
    public List<Map<String, Object>> searchHighLightPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多个高亮显示
        highlightBuilder.preTags("<span style ='color : red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);


        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");//找到我们已经高亮的标题
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//这是我们原本的map
            //将原来的标题用高亮的标题替换掉
            if (title != null) {
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text fragment : fragments) {
                    n_title += fragment;
                }
                //执行替换
                sourceAsMap.put("title", n_title);
//                System.out.println(sourceAsMap.get("title"));
            }
            list.add(sourceAsMap);
        }
        return list;
    }

}
```

