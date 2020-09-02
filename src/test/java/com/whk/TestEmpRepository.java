package com.whk;

import com.whk.elasticsearch.dao.EmpRepository;
import com.whk.entity.Emp;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
public class TestEmpRepository {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testSave() {
        Emp s = new Emp();
        s.setId(UUID.randomUUID().toString());
        s.setName("凯凯");
        s.setBir(new Date());
        s.setAge(22);
        s.setAddress("深圳");
        s.setContent("好好努力");
        empRepository.save(s);
    }

    @Test
    public void testFindOne() {
        Optional<Emp> optional = empRepository.findById("68e661aa-f758-4e28-9f0f-a432bd1e3366");
        System.out.println(optional.get());
    }

    @Test
    public void testFindAll() {

        Iterable<Emp> all = empRepository.findAll(Sort.by(Sort.Order.asc("age")));
        all.forEach(emp-> System.out.println(emp));
    }

    @Test
    public void testFindPage() {
        Iterable<Emp> search = empRepository.search(QueryBuilders.matchAllQuery(), PageRequest.of(0,20));
        search.forEach(emp-> System.out.println(emp));
    }

    @Test
    public void testFindByName() {
        List<Emp> emps = empRepository.findByName("楷楷");
        emps.forEach(emp-> System.out.println(emp));
    }

    @Test
    public void testFindByAge() {
        List<Emp> emps = empRepository.findByAge(23);
        emps.forEach(emp-> System.out.println(emp));
    }

    @Test
    public void testFindByNameAndAddress() {
        List<Emp> emps = empRepository.findByNameAndAddress("楷楷", "普宁");
        emps.forEach(emp-> System.out.println(emp));
    }

    @Test
    public void testFindByNameOrAge() {
        List<Emp> emps = empRepository.findByNameOrAge("楷楷", 23);
        emps.forEach(emp -> System.out.println(emp));
    }

    @Test
    public void testFindByAgeGreaterThan() {
        List<Emp> emps = empRepository.findByAgeGreaterThanEqual(23);
        emps.forEach(emp -> System.out.println(emp));
    }

    @Test
    public void testSearchQuery() throws IOException, ParseException {
        List<Emp> emps = new ArrayList<Emp>();


        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("ems");

        // 创建搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("content","好好"))// 设置条件
                .sort("age", SortOrder.DESC) // 排序
                .from(0) // 起始条数（当前页-1）*size的值
                .size(20) // 每页显示条数
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>"));

        searchRequest.types("emp").source(searchSourceBuilder);

        // 执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            // 原始文档
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Emp emp = new Emp();
            emp.setId(hit.getId());
            emp.setAge(Integer.parseInt(sourceAsMap.get("age").toString()));
            emp.setBir(new SimpleDateFormat("yyyy-MM-dd").parse(sourceAsMap.get("bir").toString()));
            emp.setContent(sourceAsMap.get("content").toString());
            emp.setName(sourceAsMap.get("name").toString());
            emp.setAddress(sourceAsMap.get("address").toString());

            // 高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields.containsKey("content")){
                emp.setContent(highlightFields.get("content").fragments()[0].toString());
            }
            if(highlightFields.containsKey("name")){
                emp.setName(highlightFields.get("name").fragments()[0].toString());
            }
            if(highlightFields.containsKey("address")){
                emp.setAddress(highlightFields.get("address").fragments()[0].toString());
            }

            // 放入集合
            emps.add(emp);
        }

        emps.forEach(emp -> System.out.println(emp));

    }
}
