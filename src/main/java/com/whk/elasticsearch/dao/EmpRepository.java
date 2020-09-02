package com.whk.elasticsearch.dao;

import com.whk.entity.Emp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EmpRepository extends ElasticsearchRepository<Emp,String> {

    List<Emp> findByName(String name);

    List<Emp> findByAge(Integer age);

    List<Emp> findByNameAndAddress(String name,String address);

    List<Emp> findByNameOrAge(String name,Integer age);

    List<Emp> findByAgeGreaterThanEqual(Integer age);
}
