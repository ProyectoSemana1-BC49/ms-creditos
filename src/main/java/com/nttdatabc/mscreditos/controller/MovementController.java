package com.nttdatabc.mscreditos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdatabc.mscreditos.api.MovementCreditsApi;
import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.service.MovementService;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

import static com.nttdatabc.mscreditos.utils.Constantes.PREFIX_PATH;

import java.util.List;



@RestController
@RequestMapping(PREFIX_PATH)
public class MovementController implements MovementCreditsApi {

    @Autowired
    private MovementService movementService;

    @Override
    public ResponseEntity<Void> createMovementCredit(MovementCredit movementCredit) {
         try {
            movementService.createMovementCreditService(movementCredit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<MovementCredit>> getMovementsCreditsByCreditId(String creditId) {
        List<MovementCredit> listFound = null;
        try {
            listFound = movementService.getMovementsCreditsByCreditIdService(creditId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(listFound, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MovementCredit> getMovementCreditById(String movementId) {
        MovementCredit movementById = null;
        try {
            movementById = movementService.getMovementCreditByIdService(movementId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(movementById, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createPaymentInstallmentByMovementId(String movementId, PaidInstallment paidInstallment) {
        try {
            movementService.createPaymentInstallmentByMovementId(movementId, paidInstallment);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateMovementCredit(MovementCredit movementCredit) {
        try {
            movementService.updateMovementCreditService(movementCredit);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteMovementCredit(String movementId) {
        try {
            movementService.deleteMovementCredit(movementId);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    

    
    
    
}
