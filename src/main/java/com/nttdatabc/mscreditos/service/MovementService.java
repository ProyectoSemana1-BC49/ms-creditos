package com.nttdatabc.mscreditos.service;

import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_AMOUNT_CREDIT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_INSTALLMENTS;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_PAYMENT_LIMIT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_REQUEST;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_VALUE_MIN_MOVEMENT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_NOT_FOUND_RECURSO;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_VALUE_EMPTY;
import static com.nttdatabc.mscreditos.utils.Constantes.MAX_SIZE_INSTALLMENTS;
import static com.nttdatabc.mscreditos.utils.Constantes.VALUE_MIN_ACCOUNT_BANK;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.text.html.Option;

import org.checkerframework.checker.units.qual.A;
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
public class MovementService {
    
    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private CreditService creditService;

    public void createMovementCreditService(MovementCredit movementCredit)throws ErrorResponseException{
        validateMovementNoNulls(movementCredit);
        validateMovementEmpty(movementCredit);
        validateMovementEmpty(movementCredit);
        validateCreditRegister(movementCredit.getCreditId());
        verifyValues(movementCredit);

        Credit infoCreditById = creditService.getCreditByIdService(movementCredit.getCreditId());
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
        creditService.updateCreditService(infoCreditById);
    }

    public List<MovementCredit> getMovementsCreditsByCreditIdService(String creditId)throws ErrorResponseException{
        validateCreditRegister(creditId);
        return movementRepository.findByCreditId(creditId);
    }

    public MovementCredit getMovementCreditByIdService(String movementId)throws ErrorResponseException{
        Optional<MovementCredit> movement = movementRepository.findById(movementId);
        return movement.orElseThrow(() -> new ErrorResponseException(EX_NOT_FOUND_RECURSO, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
    }
    
    public void createPaymentInstallmentByMovementId(String movementId, PaidInstallment paidInstallment)throws ErrorResponseException{
        
        if(!paymentIsValid(paidInstallment))
            throw new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    
        MovementCredit movementCredit = getMovementCreditByIdService(movementId);
        if(movementCredit.getTotalInstallments().intValue() <= movementCredit.getPaidInstallments().size()){
            throw new ErrorResponseException(EX_ERROR_PAYMENT_LIMIT, HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
        }
        
        validateCreditRegister(movementCredit.getCreditId());
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
    
    public void updateMovementCreditService(MovementCredit movementCredit)throws ErrorResponseException{
        MovementCredit movementFound = getMovementCreditByIdService(movementCredit.getId());
        movementFound.setAmount(movementCredit.getAmount());
        movementFound.setStatus(movementCredit.getStatus());
        movementFound.setDueDate(movementCredit.getDueDate());
        movementFound.setPaidInstallments(movementCredit.getPaidInstallments());
        movementFound.setTotalInstallments(movementCredit.getTotalInstallments());
        movementRepository.save(movementFound);
    }
    
    public void deleteMovementCredit(String movementId)throws ErrorResponseException{
        Optional<MovementCredit>movementFindByIdOptional = movementRepository.findById(movementId);
        if(movementFindByIdOptional.isEmpty()){
            throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }
        movementRepository.delete(movementFindByIdOptional.get());
    }
    
    /*================================================================================================ */
    public  void validateMovementNoNulls(MovementCredit movement) throws ErrorResponseException {
        Optional.of(movement)
                .filter(m -> m.getCreditId() != null)
                .filter(m -> m.getTotalInstallments() != null)
                .filter(m -> m.getAmount() != null)
                .filter(m -> m.getDueDate() != null)
                .orElseThrow(() -> new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public  void validateMovementEmpty(MovementCredit movement) throws ErrorResponseException {
        Optional.of(movement)
                .filter(m -> !m.getCreditId().isBlank())
                .filter(m -> !m.getTotalInstallments().toString().isBlank())
                .filter(m -> !m.getAmount().toString().isBlank())
                .filter(m -> !m.getDueDate().isBlank())
                .orElseThrow(() -> new ErrorResponseException(EX_VALUE_EMPTY,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public void validateCreditRegister(String creditId) throws ErrorResponseException {
        creditService.getCreditByIdService(creditId);
    }
    public void verifyValues(MovementCredit movement)throws ErrorResponseException{
        if(movement.getAmount().doubleValue() <= VALUE_MIN_ACCOUNT_BANK || movement.getTotalInstallments() <= VALUE_MIN_ACCOUNT_BANK ){
            throw new ErrorResponseException(EX_ERROR_VALUE_MIN_MOVEMENT,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public Boolean paymentIsValid(PaidInstallment paidInstallment){
        Predicate<PaidInstallment> isValidate = payment -> payment.getAmount() != null && !payment.getAmount().toString().isBlank();
        return isValidate.test(paidInstallment); 
    }

}
