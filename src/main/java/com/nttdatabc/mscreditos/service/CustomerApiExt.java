package com.nttdatabc.mscreditos.service;

import com.nttdatabc.mscreditos.model.CustomerExt;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;

import java.util.Optional;

public interface CustomerApiExt {
    Optional<CustomerExt> getCustomerById(String id) throws ErrorResponseException;
}
