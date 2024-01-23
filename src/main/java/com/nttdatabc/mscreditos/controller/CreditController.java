package com.nttdatabc.mscreditos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdatabc.mscreditos.api.CreditsApi;
import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.service.CreditServiceImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

import static com.nttdatabc.mscreditos.utils.Constantes.PREFIX_PATH;

import java.util.List;

@RestController
@RequestMapping(PREFIX_PATH)
public class CreditController implements CreditsApi{

    @Autowired
    private CreditServiceImpl creditServiceImpl;

    @Override
    public ResponseEntity<List<Credit>> getAllCredits() {
        return new ResponseEntity<>(creditServiceImpl.getAllCreditsService(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Credit> getCreditById(String creditId) {
         Credit creditById = null;
        try {
            creditById = creditServiceImpl.getCreditByIdService(creditId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(creditById, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createCredit(Credit credit) {
        try {
            creditServiceImpl.createCreditService(credit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateCredit(Credit credit) {
        try {
            creditServiceImpl.updateCreditService(credit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteCreditById(String creditId) {
        try {
            creditServiceImpl.deleteCreditById(creditId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Credit>> getCreditsByCustomerId(String customerId) {
        List<Credit>listCreditByCustomer = null;
        try {
            listCreditByCustomer = creditServiceImpl.getCreditsByCustomerId(customerId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(listCreditByCustomer, HttpStatus.OK);
    }

    
    
    
    

    
}
