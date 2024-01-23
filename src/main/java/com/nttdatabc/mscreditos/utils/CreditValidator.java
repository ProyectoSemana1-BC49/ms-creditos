package com.nttdatabc.mscreditos.utils;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.CustomerExt;
import com.nttdatabc.mscreditos.model.TypeCredit;
import com.nttdatabc.mscreditos.service.CustomerApiExtImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

import static com.nttdatabc.mscreditos.utils.Constantes.*;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_NOT_FOUND_RECURSO;

public class CreditValidator {
    public static void validateCreditsNoNulls(Credit credit) throws ErrorResponseException {
        Optional.of(credit)
                .filter(c -> c.getCustomerId() != null)
                .filter(c -> c.getMountLimit() != null)
                .filter(c -> c.getTypeCredit() != null)
                .orElseThrow(() -> new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public static void validateCreditsEmpty(Credit credit) throws ErrorResponseException {
        Optional.of(credit)
                .filter(c -> !c.getCustomerId().isEmpty())
                .filter(c -> !c.getMountLimit().toString().isBlank())
                .filter(c -> !c.getTypeCredit().isBlank())
                .orElseThrow(() -> new ErrorResponseException(EX_VALUE_EMPTY,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public static void verifyTypeCredits(Credit credit)throws ErrorResponseException{
        Predicate<Credit> existTypeCredit = creditValidate -> creditValidate
                .getTypeCredit()
                .equalsIgnoreCase(TypeCredit.PERSONAL.toString()) ||
                creditValidate.getTypeCredit().equalsIgnoreCase(TypeCredit.EMPRESA.toString())
                || creditValidate.getTypeCredit().equalsIgnoreCase(TypeCredit.TARJETA.toString());
        if(existTypeCredit.negate().test(credit)){
            throw new ErrorResponseException(EX_ERROR_TYPE_ACCOUNT,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public static void verifyValues(Credit credit)throws ErrorResponseException{
        if(credit.getMountLimit().doubleValue() <= VALUE_MIN_ACCOUNT_BANK){
            throw new ErrorResponseException(EX_ERROR_VALUE_MIN,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public static CustomerExt verifyCustomerExists(String customerId, CustomerApiExtImpl customerApiExtImpl) throws ErrorResponseException {
        try{
            Optional<CustomerExt>customerExtOptional = customerApiExtImpl.getCustomerById(customerId);
            return customerExtOptional.get();
        }catch (Exception e){
            throw new ErrorResponseException(EX_NOT_FOUND_RECURSO, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }
    }
}
