package com.library.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.library.app.IntegrationTest;
import com.library.app.domain.Checkout;
import com.library.app.repository.CheckoutRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CheckoutResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CheckoutResourceIT {

    private static final LocalDate DEFAULT_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_RETURNED = false;
    private static final Boolean UPDATED_IS_RETURNED = true;

    private static final String ENTITY_API_URL = "/api/checkouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Mock
    private CheckoutRepository checkoutRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckoutMockMvc;

    private Checkout checkout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createEntity(EntityManager em) {
        Checkout checkout = new Checkout().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME).isReturned(DEFAULT_IS_RETURNED);
        return checkout;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createUpdatedEntity(EntityManager em) {
        Checkout checkout = new Checkout().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);
        return checkout;
    }

    @BeforeEach
    public void initTest() {
        checkout = createEntity(em);
    }

    @Test
    @Transactional
    void createCheckout() throws Exception {
        int databaseSizeBeforeCreate = checkoutRepository.findAll().size();
        // Create the Checkout
        restCheckoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkout)))
            .andExpect(status().isCreated());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeCreate + 1);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testCheckout.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testCheckout.getIsReturned()).isEqualTo(DEFAULT_IS_RETURNED);
    }

    @Test
    @Transactional
    void createCheckoutWithExistingId() throws Exception {
        // Create the Checkout with an existing ID
        checkout.setId(1L);

        int databaseSizeBeforeCreate = checkoutRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkout)))
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCheckouts() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkout.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].isReturned").value(hasItem(DEFAULT_IS_RETURNED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCheckoutsWithEagerRelationshipsIsEnabled() throws Exception {
        when(checkoutRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCheckoutMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(checkoutRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCheckoutsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(checkoutRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCheckoutMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(checkoutRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get the checkout
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL_ID, checkout.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkout.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.isReturned").value(DEFAULT_IS_RETURNED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCheckout() throws Exception {
        // Get the checkout
        restCheckoutMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();

        // Update the checkout
        Checkout updatedCheckout = checkoutRepository.findById(checkout.getId()).get();
        // Disconnect from session so that the updates on updatedCheckout are not directly saved in db
        em.detach(updatedCheckout);
        updatedCheckout.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);

        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCheckout.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCheckout))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCheckout.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCheckout.getIsReturned()).isEqualTo(UPDATED_IS_RETURNED);
    }

    @Test
    @Transactional
    void putNonExistingCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkout.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(checkout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(checkout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkout)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);

        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckout))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCheckout.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCheckout.getIsReturned()).isEqualTo(UPDATED_IS_RETURNED);
    }

    @Test
    @Transactional
    void fullUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);

        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckout))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCheckout.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testCheckout.getIsReturned()).isEqualTo(UPDATED_IS_RETURNED);
    }

    @Test
    @Transactional
    void patchNonExistingCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkout.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(checkout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(checkout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(checkout)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        int databaseSizeBeforeDelete = checkoutRepository.findAll().size();

        // Delete the checkout
        restCheckoutMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkout.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Checkout> checkoutList = checkoutRepository.findAll();
        assertThat(checkoutList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
