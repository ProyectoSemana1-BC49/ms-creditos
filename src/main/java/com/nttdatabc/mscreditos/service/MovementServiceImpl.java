package com.nttdatabc.mscreditos.service;

import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_AMOUNT_CREDIT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_INSTALLMENTS;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_PAYMENT_LIMIT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_REQUEST;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_NOT_FOUND_RECURSO;
import static com.nttdatabc.mscreditos.utils.Constantes.MAX_SIZE_INSTALLMENTS;
import static com.nttdatabc.mscreditos.utils.MovementValidator.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.model.StatusCredit;
import com.nttdatabc.mscreditos.repository.MovementRepository;
import com.nttdatabc.mscreditos.utils.Utilitarios;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

@Service
public class MovementServiceImpl implements MovementService {
    
    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private CreditServiceImpl creditServiceImpl;

    @Override
    public void createMovementCreditService(MovementCredit movementCredit)throws ErrorResponseException{
        validateMovementNoNulls(movementCredit);
        validateMovementEmpty(movementCredit);
        validateMovementEmpty(movementCredit);
        validateCreditRegister(movementCredit.getCreditId(), creditServiceImpl);
        verifyValues(movementCredit);

        Credit infoCreditById = creditServiceImpl.getCreditByIdService(movementCredit.getCreditId());
        if(infoCreditById.getMountLimit().doubleValue() < movementCredit.getAmount().doubleValue()){
            throw new ErrorResponseException(EX_ERROR_AMOUNT_CREDIT, HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
        }
        if(movementCredit.getTotalInstallments() > MAX_SIZE_INSTALLMENTS){
            throw new ErrorResponseException(EX_ERROR_INSTALLMENTS, HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
        }
        
        movementCredit.setId(Utilitarios.generateUUID());
        movementCredit.setDayCreated(LocalDateTime.now().toString());
        movementCredit.setStatus(StatusCredit.ACTIVO.toString());
        movementCredit.setPaidInstallments(new ArrayList<PaidInstallment>());
        movementRepository.save(movementCredit);

        // actualizar crédito
        infoCreditById.setMountLimit(infoCreditById.getMountLimit().subtract(movementCredit.getAmount()));
        creditServiceImpl.updateCreditService(infoCreditById);
    }
    @Override
    public List<MovementCredit> getMovementsCreditsByCreditIdService(String creditId)throws ErrorResponseException{
        validateCreditRegister(creditId, creditServiceImpl);
        return movementRepository.findByCreditId(creditId);
    }
    @Override
    public MovementCredit getMovementCreditByIdService(String movementId)throws ErrorResponseException{
        Optional<MovementCredit> movement = movementRepository.findById(movementId);
        return movement.orElseThrow(() -> new ErrorResponseException(EX_NOT_FOUND_RECURSO, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
    }
    @Override
    public void createPaymentInstallmentByMovementId(String movementId, PaidInstallment paidInstallment)throws ErrorResponseException{
        
        if(!paymentIsValid(paidInstallment))
            throw new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    
        MovementCredit movementCredit = getMovementCreditByIdService(movementId);
        if(movementCredit.getTotalInstallments().intValue() <= movementCredit.getPaidInstallments().size()){
            throw new ErrorResponseException(EX_ERROR_PAYMENT_LIMIT, HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
        }
        
        validateCreditRegister(movementCredit.getCreditId(), creditServiceImpl);
        paidInstallment.setId(Utilitarios.generateUUID());
        paidInstallment.setDatePayment(LocalDateTime.now().toString());
        paidInstallment.setInstallmentNumber(movementCredit.getPaidInstallments().size() + 1);
        movementCredit.getPaidInstallments().add(paidInstallment);

        updateMovementCreditService(movementCredit);
        MovementCredit movementCreditVerify = getMovementCreditByIdService(movementId);
        if(movementCreditVerify.getTotalInstallments().intValue() == movementCredit.getPaidInstallments().size()){
            movementCreditVerify.setStatus(StatusCredit.PAGADO.toString());
            updateMovementCreditService(movementCreditVerify);
        }
    }
    @Override
    public void updateMovementCreditService(MovementCredit movementCredit)throws ErrorResponseException{
        MovementCredit movementFound = getMovementCreditByIdService(movementCredit.getId());
        movementFound.setAmount(movementCredit.getAmount());
        movementFound.setStatus(movementCredit.getStatus());
        movementFound.setDueDate(movementCredit.getDueDate());
        movementFound.setPaidInstallments(movementCredit.getPaidInstallments());
        movementFound.setTotalInstallments(movementCredit.getTotalInstallments());
        movementRepository.save(movementFound);
    }
    @Override
    public void deleteMovementCredit(String movementId)throws ErrorResponseException{
        Optional<MovementCredit>movementFindByIdOptional = movementRepository.findById(movementId);
        if(movementFindByIdOptional.isEmpty()){
            throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }
        movementRepository.delete(movementFindByIdOptional.get());
    }

}
