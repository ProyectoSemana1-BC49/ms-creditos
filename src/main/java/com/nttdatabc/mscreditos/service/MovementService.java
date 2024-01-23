package com.nttdatabc.mscreditos.service;

import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import java.util.List;

/**
 * Repository movement.
 */
public interface MovementService {
  void createMovementCreditService(MovementCredit movementCredit) throws ErrorResponseException;

  List<MovementCredit> getMovementsCreditsByCreditIdService(String creditId) throws ErrorResponseException;

  MovementCredit getMovementCreditByIdService(String movementId) throws ErrorResponseException;

  void createPaymentInstallmentByMovementId(String movementId, PaidInstallment paidInstallment) throws ErrorResponseException;

  void updateMovementCreditService(MovementCredit movementCredit) throws ErrorResponseException;

  void deleteMovementCredit(String movementId) throws ErrorResponseException;
}
