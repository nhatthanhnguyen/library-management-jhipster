package com.library.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.library.app.IntegrationTest;
import com.library.app.domain.Hold;
import com.library.app.repository.HoldRepository;
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
 * Integration tests for the {@link HoldResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HoldResourceIT {

    private static final LocalDate DEFAULT_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/holds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HoldRepository holdRepository;

    @Mock
    private HoldRepository holdRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHoldMockMvc;

    private Hold hold;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createEntity(EntityManager em) {
        Hold hold = new Hold().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
        return hold;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createUpdatedEntity(EntityManager em) {
        Hold hold = new Hold().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        return hold;
    }

    @BeforeEach
    public void initTest() {
        hold = createEntity(em);
    }

    @Test
    @Transactional
    void createHold() throws Exception {
        int databaseSizeBeforeCreate = holdRepository.findAll().size();
        // Create the Hold
        restHoldMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hold)))
            .andExpect(status().isCreated());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeCreate + 1);
        Hold testHold = holdList.get(holdList.size() - 1);
        assertThat(testHold.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testHold.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    void createHoldWithExistingId() throws Exception {
        // Create the Hold with an existing ID
        hold.setId(1L);

        int databaseSizeBeforeCreate = holdRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHoldMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hold)))
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHolds() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hold.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHoldsWithEagerRelationshipsIsEnabled() throws Exception {
        when(holdRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHoldMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(holdRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHoldsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(holdRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHoldMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(holdRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get the hold
        restHoldMockMvc
            .perform(get(ENTITY_API_URL_ID, hold.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hold.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHold() throws Exception {
        // Get the hold
        restHoldMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        int databaseSizeBeforeUpdate = holdRepository.findAll().size();

        // Update the hold
        Hold updatedHold = holdRepository.findById(hold.getId()).get();
        // Disconnect from session so that the updates on updatedHold are not directly saved in db
        em.detach(updatedHold);
        updatedHold.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHold.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
        Hold testHold = holdList.get(holdList.size() - 1);
        assertThat(testHold.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testHold.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void putNonExistingHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hold.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hold)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        int databaseSizeBeforeUpdate = holdRepository.findAll().size();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
        Hold testHold = holdList.get(holdList.size() - 1);
        assertThat(testHold.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testHold.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void fullUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        int databaseSizeBeforeUpdate = holdRepository.findAll().size();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
        Hold testHold = holdList.get(holdList.size() - 1);
        assertThat(testHold.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testHold.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hold.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hold))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHold() throws Exception {
        int databaseSizeBeforeUpdate = holdRepository.findAll().size();
        hold.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(hold)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        int databaseSizeBeforeDelete = holdRepository.findAll().size();

        // Delete the hold
        restHoldMockMvc
            .perform(delete(ENTITY_API_URL_ID, hold.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Hold> holdList = holdRepository.findAll();
        assertThat(holdList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
