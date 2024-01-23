package com.nttdatabc.mscreditos;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.MovementCredit;
import com.nttdatabc.mscreditos.model.PaidInstallment;
import com.nttdatabc.mscreditos.model.TypeCredit;
import com.nttdatabc.mscreditos.repository.MovementRepository;
import com.nttdatabc.mscreditos.service.CreditServiceImpl;
import com.nttdatabc.mscreditos.service.MovementServiceImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MovementTest {
  @Mock
  private MovementRepository movementRepository;

  @Mock
  private CreditServiceImpl creditServiceImpl;

  @InjectMocks
  private MovementServiceImpl movementService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void createMovementCreditServiceTest() throws ErrorResponseException {
    // Arrange
    MovementCredit movementCreditMock = new MovementCredit();
    movementCreditMock.setCreditId("testCreditId");
    movementCreditMock.setAmount(BigDecimal.valueOf(500));
    movementCreditMock.setTotalInstallments(3);
    movementCreditMock.setPaidInstallments(new ArrayList<>());
    movementCreditMock.setDueDate("2020-01-01");
    movementCreditMock.setDayCreated("2020-01-01");
    movementCreditMock.setTotalInstallments(2);
    movementCreditMock.setId("id");

    Credit infoCreditMock = new Credit();
    infoCreditMock.setId("testCreditId");
    infoCreditMock.setMountLimit(BigDecimal.valueOf(1000));
    infoCreditMock.setTypeCredit(TypeCredit.EMPRESA.toString());
    infoCreditMock.setCustomerId("34234543");

    when(creditServiceImpl.getCreditByIdService("testCreditId")).thenReturn(infoCreditMock);
    when(movementRepository.save(any(MovementCredit.class))).thenReturn(movementCreditMock);

    // Act
    assertDoesNotThrow(() -> movementService.createMovementCreditService(movementCreditMock));

    // Assert
    when(creditServiceImpl.getCreditByIdService("testCreditId")).thenReturn(infoCreditMock);
    verify(creditServiceImpl, times(1)).updateCreditService(infoCreditMock);
    verify(movementRepository, times(1)).save(movementCreditMock);
  }

  @Test
  void createMovementCreditServiceAmountConflictTest() throws ErrorResponseException {
    // Arrange
    MovementCredit movementCreditMock = new MovementCredit();
    movementCreditMock.setCreditId("testCreditId");
    movementCreditMock.setAmount(BigDecimal.valueOf(1500));
    movementCreditMock.setTotalInstallments(3);

    Credit infoCreditMock = new Credit();
    infoCreditMock.setId("testCreditId");
    infoCreditMock.setMountLimit(BigDecimal.valueOf(1000));

    when(creditServiceImpl.getCreditByIdService("testCreditId")).thenReturn(infoCreditMock);

    // Act and Assert
    assertThrows(ErrorResponseException.class, () -> movementService.createMovementCreditService(movementCreditMock));
    verify(creditServiceImpl, never()).updateCreditService(any());
    verify(movementRepository, never()).save(any(MovementCredit.class));
  }
  @Test
  void createMovementCreditServiceInstallmentsConflictTest() throws ErrorResponseException {
    // Arrange
    MovementCredit movementCreditMock = new MovementCredit();
    movementCreditMock.setCreditId("testCreditId");
    movementCreditMock.setAmount(BigDecimal.valueOf(500));
    movementCreditMock.setTotalInstallments(10);

    Credit infoCreditMock = new Credit();
    infoCreditMock.setId("testCreditId");
    infoCreditMock.setMountLimit(BigDecimal.valueOf(1000));

    when(creditServiceImpl.getCreditByIdService("testCreditId")).thenReturn(infoCreditMock);

    // Act and Assert
    assertThrows(ErrorResponseException.class, () -> movementService.createMovementCreditService(movementCreditMock));
    verify(creditServiceImpl, never()).updateCreditService(any());
    verify(movementRepository, never()).save(any(MovementCredit.class));
  }
  @Test
  void getMovementsCreditsByCreditIdServiceTest() throws ErrorResponseException {
    // Arrange
    String testCreditId = "testCreditId";

    Credit infoCreditMock = new Credit();
    infoCreditMock.setId(testCreditId);

    List<MovementCredit> movementsList = Collections.singletonList(new MovementCredit());

    when(creditServiceImpl.getCreditByIdService(testCreditId)).thenReturn(infoCreditMock);
    when(movementRepository.findByCreditId(testCreditId)).thenReturn(movementsList);

    // Act
    List<MovementCredit> result = assertDoesNotThrow(() -> movementService.getMovementsCreditsByCreditIdService(testCreditId));

    // Assert
    verify(creditServiceImpl, times(1)).getCreditByIdService(testCreditId);
    verify(movementRepository, times(1)).findByCreditId(testCreditId);
  }
  @Test
  void getMovementCreditByIdServiceTest() throws ErrorResponseException {
    // Arrange
    MovementCredit movementCreditMock = new MovementCredit();
    movementCreditMock.setId("testMovementId");

    when(movementRepository.findById("testMovementId")).thenReturn(Optional.of(movementCreditMock));

    // Act
    MovementCredit result = movementService.getMovementCreditByIdService("testMovementId");

    // Assert
    assertNotNull(result);
    assertEquals("testMovementId", result.getId());
  }
}
