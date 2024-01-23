package com.nttdatabc.mscreditos.service;

import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_CONFLICTO_CUSTOMER_PERSONA;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_NOT_FOUND_RECURSO;
import static com.nttdatabc.mscreditos.utils.Constantes.INTEREST_RATE;
import static com.nttdatabc.mscreditos.utils.Constantes.MAX_SIZE_ACCOUNT_CUSTOMER_PERSONA;
import static com.nttdatabc.mscreditos.utils.CreditValidator.validateCreditsEmpty;
import static com.nttdatabc.mscreditos.utils.CreditValidator.validateCreditsNoNulls;
import static com.nttdatabc.mscreditos.utils.CreditValidator.verifyCustomerExists;
import static com.nttdatabc.mscreditos.utils.CreditValidator.verifyTypeCredits;
import static com.nttdatabc.mscreditos.utils.CreditValidator.verifyValues;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.CustomerExt;
import com.nttdatabc.mscreditos.model.TypeCustomer;
import com.nttdatabc.mscreditos.repository.CreditRepository;
import com.nttdatabc.mscreditos.utils.Utilitarios;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Credit service.
 */
@Service
public class CreditServiceImpl implements CreditService {

  @Autowired
  private CreditRepository creditRepository;
  @Autowired
  private CustomerApiExtImpl customerApiExtImpl;

  @Override
  public List<Credit> getAllCreditsService() {
    return creditRepository.findAll();
  }

  @Override
  public Credit getCreditByIdService(String creditId) throws ErrorResponseException {
    Optional<Credit> credit = creditRepository.findById(creditId);
    return credit.orElseThrow(() -> new ErrorResponseException(EX_NOT_FOUND_RECURSO,
        HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND));
  }

  @Override
  public void createCreditService(Credit credit) throws ErrorResponseException {
    validateCreditsNoNulls(credit);
    validateCreditsEmpty(credit);
    verifyTypeCredits(credit);
    verifyValues(credit);
    CustomerExt customerFound = verifyCustomerExists(credit.getCustomerId(), customerApiExtImpl);
    List<Credit> listCreditsByCustomer = getCreditsByCustomerId(credit.getCustomerId());

    if (customerFound.getType().equalsIgnoreCase(TypeCustomer.PERSONA.toString())) {
      if (listCreditsByCustomer.size() >= MAX_SIZE_ACCOUNT_CUSTOMER_PERSONA) {
        throw new ErrorResponseException(EX_ERROR_CONFLICTO_CUSTOMER_PERSONA,
            HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
      }
    }

    credit.setId(Utilitarios.generateUuid());
    credit.setDateOpen(LocalDateTime.now().toString());
    credit.setInterestRate(BigDecimal.valueOf(INTEREST_RATE));
    creditRepository.save(credit);
  }

  @Override
  public void updateCreditService(Credit credit) throws ErrorResponseException {
    validateCreditsNoNulls(credit);
    validateCreditsEmpty(credit);
    verifyTypeCredits(credit);
    Optional<Credit> getCreditById = creditRepository.findById(credit.getId());
    if (getCreditById.isEmpty()) {
      throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,
          HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }
    Credit creditFound = getCreditById.get();
    creditFound.setMountLimit(credit.getMountLimit());
    creditFound.setTypeCredit(credit.getTypeCredit());
    creditRepository.save(creditFound);
  }

  @Override
  public void deleteCreditById(String creditId) throws ErrorResponseException {
    Optional<Credit> creditFindByIdOptional = creditRepository.findById(creditId);
    if (creditFindByIdOptional.isEmpty()) {
      throw new ErrorResponseException(EX_NOT_FOUND_RECURSO,
          HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }
    creditRepository.delete(creditFindByIdOptional.get());
  }

  @Override
  public List<Credit> getCreditsByCustomerId(String customerId) throws ErrorResponseException {
    verifyCustomerExists(customerId, customerApiExtImpl);
    return creditRepository.findByCustomerId(customerId);
  }


}
