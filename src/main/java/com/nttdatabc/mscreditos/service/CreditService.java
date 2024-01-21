package com.nttdatabc.mscreditos.service;

import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_REQUEST;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_TYPE_ACCOUNT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_VALUE_MIN;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_NOT_FOUND_RECURSO;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_VALUE_EMPTY;
import static com.nttdatabc.mscreditos.utils.Constantes.INTEREST_RATE;
import static com.nttdatabc.mscreditos.utils.Constantes.VALUE_MIN_ACCOUNT_BANK;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.CustomerExt;
import com.nttdatabc.mscreditos.model.TypeCredit;
import com.nttdatabc.mscreditos.repository.CreditRepository;
import com.nttdatabc.mscreditos.utils.Utilitarios;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;


@Service
public class CreditService {

    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private CustomerApiExt customerApiExt;

    public List<Credit>getAllCreditsService(){
        return creditRepository.findAll();
    }

    public Credit getCreditByIdService(String creditId) throws ErrorResponseException{
        Optional<Credit> credit = creditRepository.findById(creditId);
        return credit.orElseThrow(() -> new ErrorResponseException(EX_NOT_FOUND_RECURSO, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
    }

    public void createCreditService(Credit credit)throws ErrorResponseException{
        validateCreditsNoNulls(credit);
        validateCreditsEmpty(credit);
        verifyTypeCredits(credit);
        verifyValues(credit);
        CustomerExt customerFound = verifyCustomerExists(credit.getCustomerId());

        credit.setId(Utilitarios.generateUUID());
        credit.setDateOpen(LocalDateTime.now().toString());
        credit.setInterestRate(BigDecimal.valueOf(INTEREST_RATE));
        creditRepository.save(credit);
    }

    public void updateCreditService(Credit credit)throws ErrorResponseException{
        validateCreditsNoNulls(credit);
        validateCreditsEmpty(credit);
        verifyTypeCredits(credit);
        Optional<Credit>getCreditById = creditRepository.findById(credit.getId());
        if(getCreditById.isEmpty()){
            throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }
        Credit creditFound = getCreditById.get();
        creditFound.setMountLimit(credit.getMountLimit());
        creditFound.setTypeCredit(credit.getTypeCredit());
        creditRepository.save(creditFound);
    }

    public void deleteCreditById(String creditId)throws ErrorResponseException{
        Optional<Credit>creditFindByIdOptional = creditRepository.findById(creditId);
        if(creditFindByIdOptional.isEmpty()){
            throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
        }
        creditRepository.delete(creditFindByIdOptional.get());
    }
    
    public List<Credit>getCreditsByCustomerId(String customerId) throws ErrorResponseException{
        verifyCustomerExists(customerId);
        return creditRepository.findByCustomerId(customerId);
        
    }
    
    /*====================================================================================== */
    public  void validateCreditsNoNulls(Credit credit) throws ErrorResponseException {
        Optional.of(credit)
                .filter(c -> c.getCustomerId() != null)
                .filter(c -> c.getMountLimit() != null)
                .filter(c -> c.getTypeCredit() != null)
                .orElseThrow(() -> new ErrorResponseException(EX_ERROR_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public  void validateCreditsEmpty(Credit credit) throws ErrorResponseException {
        Optional.of(credit)
                .filter(c -> !c.getCustomerId().isEmpty())
                .filter(c -> !c.getMountLimit().toString().isBlank())
                .filter(c -> !c.getTypeCredit().isBlank())
                .orElseThrow(() -> new ErrorResponseException(EX_VALUE_EMPTY,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
    }
    public  void verifyTypeCredits(Credit credit)throws ErrorResponseException{
        Predicate<Credit> existTypeCredit = creditValidate -> creditValidate
                .getTypeCredit()
                .equalsIgnoreCase(TypeCredit.PERSONAL.toString()) ||
                creditValidate.getTypeCredit().equalsIgnoreCase(TypeCredit.EMPRESA.toString())
                || creditValidate.getTypeCredit().equalsIgnoreCase(TypeCredit.TARJETA.toString());
        if(existTypeCredit.negate().test(credit)){
            throw new ErrorResponseException(EX_ERROR_TYPE_ACCOUNT,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public void verifyValues(Credit credit)throws ErrorResponseException{
        if(credit.getMountLimit().doubleValue() <= VALUE_MIN_ACCOUNT_BANK){
            throw new ErrorResponseException(EX_ERROR_VALUE_MIN,HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
        }
    }
    public CustomerExt verifyCustomerExists(String customerId) throws ErrorResponseException {
      try{
          Optional<CustomerExt>customerExtOptional = customerApiExt.getCustomerById(customerId);
          return customerExtOptional.get();
      }catch (Exception e){
          throw new ErrorResponseException(EX_NOT_FOUND_RECURSO, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
      }
    }


}
