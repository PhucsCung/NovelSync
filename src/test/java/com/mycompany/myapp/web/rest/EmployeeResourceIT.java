package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EmployeeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.EmployeeService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmployeeResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_HIRE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_HIRE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_SALARY = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALARY = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Mock
    private EmployeeService employeeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    private Employee insertedEmployee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity() {
        return new Employee().fullName(DEFAULT_FULL_NAME).hireDate(DEFAULT_HIRE_DATE).salary(DEFAULT_SALARY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity() {
        return new Employee().fullName(UPDATED_FULL_NAME).hireDate(UPDATED_HIRE_DATE).salary(UPDATED_SALARY);
    }

    @BeforeEach
    void initTest() {
        employee = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEmployee != null) {
            employeeRepository.delete(insertedEmployee);
            insertedEmployee = null;
        }
    }

    @Test
    @Transactional
    void createEmployee() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Employee
        var returnedEmployee = om.readValue(
            restEmployeeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Employee.class
        );

        // Validate the Employee in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEmployeeUpdatableFieldsEquals(returnedEmployee, getPersistedEmployee(returnedEmployee));

        insertedEmployee = returnedEmployee;
    }

    @Test
    @Transactional
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setFullName(null);

        // Create the Employee, which fails.

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHireDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setHireDate(null);

        // Create the Employee, which fails.

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSalaryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        employee.setSalary(null);

        // Create the Employee, which fails.

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get all the employeeList
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].hireDate").value(hasItem(DEFAULT_HIRE_DATE.toString())))
            .andExpect(jsonPath("$.[*].salary").value(hasItem(sameNumber(DEFAULT_SALARY))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsEnabled() throws Exception {
        when(employeeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(employeeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(employeeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(employeeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL_ID, employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.hireDate").value(DEFAULT_HIRE_DATE.toString()))
            .andExpect(jsonPath("$.salary").value(sameNumber(DEFAULT_SALARY)));
    }

    @Test
    @Transactional
    void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee);
        updatedEmployee.fullName(UPDATED_FULL_NAME).hireDate(UPDATED_HIRE_DATE).salary(UPDATED_SALARY);

        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmployeeToMatchAllProperties(updatedEmployee);
    }

    @Test
    @Transactional
    void putNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employee.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee.fullName(UPDATED_FULL_NAME).hireDate(UPDATED_HIRE_DATE).salary(UPDATED_SALARY);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmployee, employee), getPersistedEmployee(employee));
    }

    @Test
    @Transactional
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee.fullName(UPDATED_FULL_NAME).hireDate(UPDATED_HIRE_DATE).salary(UPDATED_SALARY);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmployeeUpdatableFieldsEquals(partialUpdatedEmployee, getPersistedEmployee(partialUpdatedEmployee));
    }

    @Test
    @Transactional
    void patchNonExistingEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employee.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployee() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        employee.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmployee() throws Exception {
        // Initialize the database
        insertedEmployee = employeeRepository.saveAndFlush(employee);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the employee
        restEmployeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, employee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return employeeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Employee getPersistedEmployee(Employee employee) {
        return employeeRepository.findById(employee.getId()).orElseThrow();
    }

    protected void assertPersistedEmployeeToMatchAllProperties(Employee expectedEmployee) {
        assertEmployeeAllPropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }

    protected void assertPersistedEmployeeToMatchUpdatableProperties(Employee expectedEmployee) {
        assertEmployeeAllUpdatablePropertiesEquals(expectedEmployee, getPersistedEmployee(expectedEmployee));
    }
}
