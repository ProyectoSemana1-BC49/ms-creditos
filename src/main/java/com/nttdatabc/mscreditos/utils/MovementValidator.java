package com.nttdatabc.mscreditos.utils;

import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_REQUEST;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_ERROR_VALUE_MIN_MOVEMENT;
import static com.nttdatabc.mscreditos.utils.Constantes.EX_VALUE_EMPTY;
import static com.nttdatabc.mscreditos.utils.Constantes.VALUE_MIN_ACCOUNT_BANK;

import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.service.CreditServiceImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.http.HttpStatus;

/**
 * Clase movement validator.
 */
public class MovementValidator {
  /**
   * Valida que los campos obligatorios de un movimiento de crédito no sean nulos.
   *
   * @param movement El movimiento de crédito a validar.
   * @throws ErrorResponseException Si algún campo obligatorio es nulo.
   */
  public static void validateMovementNoNulls(MovementCredit movement) throws ErrorResponseException {
    Optional.of(movement)
        .filter(m -> m.getCreditId() != null)
        .filter(m -> m.getTotalInstallments() != null)
        .filter(m -> m.getAmount() != null)
        .filter(m -> m.getDueDate() != null)
        .orElseThrow(() -> new ErrorResponseException(EX_ERROR_REQUEST,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
  }

  /**
   * Valida que los campos obligatorios de un movimiento de crédito no estén vacíos.
   *
   * @param movement El movimiento de crédito a validar.
   * @throws ErrorResponseException Si algún campo obligatorio está vacío.
   */
  public static void validateMovementEmpty(MovementCredit movement) throws ErrorResponseException {
    Optional.of(movement)
        .filter(m -> !m.getCreditId().isBlank())
        .filter(m -> !m.getTotalInstallments().toString().isBlank())
        .filter(m -> !m.getAmount().toString().isBlank())
        .filter(m -> !m.getDueDate().isBlank())
        .orElseThrow(() -> new ErrorResponseException(EX_VALUE_EMPTY,
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST));
  }

  /**
   * Verifica si un crédito con el ID proporcionado está registrado.
   *
   * @param creditId          El ID del crédito a verificar.
   * @param creditServiceImpl El servicio para obtener información del crédito.
   * @throws ErrorResponseException Si el crédito no está registrado.
   */
  public static void validateCreditRegister(String creditId, CreditServiceImpl creditServiceImpl) throws ErrorResponseException {
    creditServiceImpl.getCreditByIdService(creditId);
  }

  /**
   * Verifica que los valores de un movimiento de crédito sean válidos.
   *
   * @param movement El movimiento de crédito a verificar.
   * @throws ErrorResponseException Si algún valor no es válido.
   */
  public static void verifyValues(MovementCredit movement) throws ErrorResponseException {
    if (movement.getAmount().doubleValue() <= VALUE_MIN_ACCOUNT_BANK || movement.getTotalInstallments() <= VALUE_MIN_ACCOUNT_BANK) {
      throw new ErrorResponseException(EX_ERROR_VALUE_MIN_MOVEMENT,
          HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Verifica si un pago de cuota es válido.
   *
   * @param paidInstallment El pago de cuota a verificar.
   * @return true si el pago es válido, false si no lo es.
   */
  public static Boolean paymentIsValid(PaidInstallment paidInstallment) {
    Predicate<PaidInstallment> isValidate =
        payment -> payment.getAmount() != null && !payment.getAmount().toString().isBlank();
    return isValidate.test(paidInstallment);
  }

}
