package com.nttdatabc.mscreditos.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdatabc.mscreditos.model.Credit;



@Repository
public interface CreditRepository extends MongoRepository<Credit, String>{

    List<Credit>findByCustomerId(String customerId);
}
