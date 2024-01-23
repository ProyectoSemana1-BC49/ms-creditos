package com.nttdatabc.mscreditos.utils;

import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.service.CreditServiceImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

import static com.nttdatabc.mscreditos.utils.Constantes.*;

public class MovementValidator {
    public static void validateMovementNoNulls(MovementCredit movement) throws ErrorResponseException {
        Optional.of(movement)
                .filter(m -> m.getCreditId() != null)
                .filter(m -> m.getTotalInstallments() != null)
                .filter(m -> m.getAmount() != null)
                .filter(m -> m.getDueDate() != null)
                .orElseThrow(() -> new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public static void validateMovementEmpty(MovementCredit movement) throws ErrorResponseException {
        Optional.of(movement)
                .filter(m -> !m.getCreditId().isBlank())
                .filter(m -> !m.getTotalInstallments().toString().isBlank())
                .filter(m -> !m.getAmount().toString().isBlank())
                .filter(m -> !m.getDueDate().isBlank())
                .orElseThrow(() -> new ErrorResponseException(EX_VALUE_EMPTY,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public static void validateCreditRegister(String creditId, CreditServiceImpl creditServiceImpl) throws ErrorResponseException {
        creditServiceImpl.getCreditByIdService(creditId);
    }
    public static void verifyValues(MovementCredit movement)throws ErrorResponseException{
        if(movement.getAmount().doubleValue() <= VALUE_MIN_ACCOUNT_BANK || movement.getTotalInstallments() <= VALUE_MIN_ACCOUNT_BANK ){
            throw new ErrorResponseException(EX_ERROR_VALUE_MIN_MOVEMENT,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public static Boolean paymentIsValid(PaidInstallment paidInstallment){
        Predicate<PaidInstallment> isValidate = payment -> payment.getAmount() != null && !payment.getAmount().toString().isBlank();
        return isValidate.test(paidInstallment);
    }

}
