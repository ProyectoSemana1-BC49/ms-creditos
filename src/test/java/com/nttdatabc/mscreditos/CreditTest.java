package com.nttdatabc.mscreditos;

import com.nttdatabc.mscreditos.model.Credit;
import com.nttdatabc.mscreditos.model.CustomerExt;
import com.nttdatabc.mscreditos.model.TypeCustomer;
import com.nttdatabc.mscreditos.repository.CreditRepository;
import com.nttdatabc.mscreditos.service.CreditServiceImpl;
import com.nttdatabc.mscreditos.service.CustomerApiExtImpl;
import com.nttdatabc.mscreditos.utils.exceptions.errors.ErrorResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreditTest {
  @Mock
  private CreditRepository creditRepository;

  @Mock
  private CustomerApiExtImpl customerApiExtImpl;

  @InjectMocks
  private CreditServiceImpl creditService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getAllCreditsServiceTest() {
    // Arrange
    when(creditRepository.findAll()).thenReturn(Collections.singletonList(new Credit()));

    // Act
    List<Credit> result = creditService.getAllCreditsService();

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
  @Test
  void getCreditByIdServiceTest() throws ErrorResponseException {
    // Arrange
    Credit creditMock = new Credit();
    creditMock.setId("testCreditId");

    when(creditRepository.findById("testCreditId")).thenReturn(Optional.of(creditMock));

    // Act
    Credit result = creditService.getCreditByIdService("testCreditId");

    // Assert
    assertNotNull(result);
    assertEquals("testCreditId", result.getId());
  }
  @Test
  void createCreditServiceTest() throws ErrorResponseException {
    // Arrange
    Credit creditMock = new Credit();
    creditMock.setCustomerId("testCustomerId");
    creditMock.setMountLimit(BigDecimal.valueOf(1000));
    creditMock.setTypeCredit("Personal");

    CustomerExt customerFound = new CustomerExt();
    customerFound.setType(TypeCustomer.PERSONA.toString());

    List<Credit> listCreditsByCustomer = Collections.emptyList();

    when(customerApiExtImpl.getCustomerById("testCustomerId")).thenReturn(Optional.of(customerFound));
    when(creditRepository.findByCustomerId("testCustomerId")).thenReturn(listCreditsByCustomer);

    // Act
    assertDoesNotThrow(() -> creditService.createCreditService(creditMock));

    // Assert
    verify(creditRepository, times(1)).save(creditMock);
  }
  @Test
  void createCreditServicePersonaConflictTest() throws ErrorResponseException {
    // Arrange
    Credit creditMock = new Credit();
    creditMock.setCustomerId("testCustomerId");

    CustomerExt customerFound = new CustomerExt();
    customerFound.setType(TypeCustomer.PERSONA.toString());

    List<Credit> listCreditsByCustomer = Collections.singletonList(new Credit());

    when(customerApiExtImpl.getCustomerById("testCustomerId")).thenReturn(Optional.of(customerFound));
    when(creditRepository.findByCustomerId("testCustomerId")).thenReturn(listCreditsByCustomer);

    // Act and Assert
    assertThrows(ErrorResponseException.class, () -> creditService.createCreditService(creditMock));
    verify(creditRepository, never()).save(creditMock);
  }
  @Test
  void updateCreditServiceTest() throws ErrorResponseException {
    // Arrange
    Credit creditMock = new Credit();
    creditMock.setId("testCreditId");
    creditMock.setMountLimit(BigDecimal.valueOf(2000));
    creditMock.setTypeCredit("PERSONAL");
    creditMock.setCustomerId("customer");
    creditMock.setInterestRate(BigDecimal.valueOf(22));
    creditMock.setDateOpen("2020-01-01");

    when(creditRepository.findById("testCreditId")).thenReturn(Optional.of(creditMock));

    // Act
    assertDoesNotThrow(() -> creditService.updateCreditService(creditMock));

    // Assert
    verify(creditRepository, times(1)).save(creditMock);
  }
  @Test
  void deleteCreditByIdServiceTest() throws ErrorResponseException {
    // Arrange
    Credit creditMock = new Credit();
    creditMock.setId("testCreditId");

    when(creditRepository.findById("testCreditId")).thenReturn(Optional.of(creditMock));

    // Act
    assertDoesNotThrow(() -> creditService.deleteCreditById("testCreditId"));

    // Assert
    verify(creditRepository, times(1)).delete(creditMock);
  }
  @Test
  void deleteCreditByIdServiceNotFoundTest() {
    // Arrange
    when(creditRepository.findById("testCreditId")).thenReturn(Optional.empty());

    // Act and Assert
    assertThrows(ErrorResponseException.class, () -> creditService.deleteCreditById("testCreditId"));
    verify(creditRepository, never()).delete(any());
  }
  @Test
  void getCreditsByCustomerIdServiceTest() throws ErrorResponseException {
    // Arrange
    String testCustomerId = "testCustomerId";

    CustomerExt customerFound = new CustomerExt();
    customerFound.set_id(testCustomerId);

    List<Credit> creditsList = Collections.singletonList(new Credit());

    when(customerApiExtImpl.getCustomerById(testCustomerId)).thenReturn(Optional.of(customerFound));
    when(creditRepository.findByCustomerId(testCustomerId)).thenReturn(creditsList);

    // Act
    List<Credit> result = assertDoesNotThrow(() -> creditService.getCreditsByCustomerId(testCustomerId));

    // Assert
    verify(customerApiExtImpl, times(1)).getCustomerById(testCustomerId);
    verify(creditRepository, times(1)).findByCustomerId(testCustomerId);
  }
}
