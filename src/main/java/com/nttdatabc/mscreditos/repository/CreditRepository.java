package com.nttdatabc.mscreditos.repository;

import com.nttdatabc.mscreditos.model.Credit;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository credit.
 */
@Repository
public interface CreditRepository extends MongoRepository<Credit, String> {

  List<Credit> findByCustomerId(String customerId);
}
