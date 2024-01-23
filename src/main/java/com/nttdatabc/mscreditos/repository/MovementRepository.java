package com.nttdatabc.mscreditos.repository;

import com.nttdatabc.mscreditos.model.MovementCredit;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Movement repository.
 */
@Repository
public interface MovementRepository extends MongoRepository<MovementCredit, String> {
  List<MovementCredit> findByCreditId(String creditId);
}
