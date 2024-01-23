package com.nttdatabc.mscreditos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdatabc.mscreditos.api.MovementCreditsApi;
import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.service.MovementServiceImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

import static com.nttdatabc.mscreditos.utils.Constantes.PREFIX_PATH;

import java.util.List;



@RestController
@RequestMapping(PREFIX_PATH)
public class MovementController implements MovementCreditsApi {

    @Autowired
    private MovementServiceImpl movementServiceImpl;

    @Override
    public ResponseEntity<Void> createMovementCredit(MovementCredit movementCredit) {
         try {
            movementServiceImpl.createMovementCreditService(movementCredit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<MovementCredit>> getMovementsCreditsByCreditId(String creditId) {
        List<MovementCredit> listFound = null;
        try {
            listFound = movementServiceImpl.getMovementsCreditsByCreditIdService(creditId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(listFound, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MovementCredit> getMovementCreditById(String movementId) {
        MovementCredit movementById = null;
        try {
            movementById = movementServiceImpl.getMovementCreditByIdService(movementId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(movementById, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createPaymentInstallmentByMovementId(String movementId, PaidInstallment paidInstallment) {
        try {
            movementServiceImpl.createPaymentInstallmentByMovementId(movementId, paidInstallment);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateMovementCredit(MovementCredit movementCredit) {
        try {
            movementServiceImpl.updateMovementCreditService(movementCredit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteMovementCredit(String movementId) {
        try {
            movementServiceImpl.deleteMovementCredit(movementId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    

    
    
    
}
