package com.nttdatabc.mscreditos.service;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

import java.util.List;

public interface CreditService {
    List<Credit> getAllCreditsService();
    Credit getCreditByIdService(String creditId) throws ErrorResponseException;
    void createCreditService(Credit credit)throws ErrorResponseException;
    void updateCreditService(Credit credit)throws ErrorResponseException;
    void deleteCreditById(String creditId)throws ErrorResponseException;
    List<Credit>getCreditsByCustomerId(String customerId)throws ErrorResponseException;
}
