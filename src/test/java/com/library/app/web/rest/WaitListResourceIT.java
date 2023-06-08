package com.library.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.library.app.IntegrationTest;
import com.library.app.domain.WaitList;
import com.library.app.repository.WaitListRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WaitListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WaitListResourceIT {

    private static final String ENTITY_API_URL = "/api/wait-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WaitListRepository waitListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWaitListMockMvc;

    private WaitList waitList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitList createEntity(EntityManager em) {
        WaitList waitList = new WaitList();
        return waitList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitList createUpdatedEntity(EntityManager em) {
        WaitList waitList = new WaitList();
        return waitList;
    }

    @BeforeEach
    public void initTest() {
        waitList = createEntity(em);
    }

    @Test
    @Transactional
    void createWaitList() throws Exception {
        int databaseSizeBeforeCreate = waitListRepository.findAll().size();
        // Create the WaitList
        restWaitListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(waitList)))
            .andExpect(status().isCreated());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeCreate + 1);
        WaitList testWaitList = waitListList.get(waitListList.size() - 1);
    }

    @Test
    @Transactional
    void createWaitListWithExistingId() throws Exception {
        // Create the WaitList with an existing ID
        waitList.setId(1L);

        int databaseSizeBeforeCreate = waitListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWaitListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(waitList)))
            .andExpect(status().isBadRequest());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWaitLists() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        // Get all the waitListList
        restWaitListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waitList.getId().intValue())));
    }

    @Test
    @Transactional
    void getWaitList() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        // Get the waitList
        restWaitListMockMvc
            .perform(get(ENTITY_API_URL_ID, waitList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(waitList.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingWaitList() throws Exception {
        // Get the waitList
        restWaitListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWaitList() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();

        // Update the waitList
        WaitList updatedWaitList = waitListRepository.findById(waitList.getId()).get();
        // Disconnect from session so that the updates on updatedWaitList are not directly saved in db
        em.detach(updatedWaitList);

        restWaitListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWaitList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWaitList))
            )
            .andExpect(status().isOk());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
        WaitList testWaitList = waitListList.get(waitListList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, waitList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(waitList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(waitList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(waitList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWaitListWithPatch() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();

        // Update the waitList using partial update
        WaitList partialUpdatedWaitList = new WaitList();
        partialUpdatedWaitList.setId(waitList.getId());

        restWaitListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWaitList))
            )
            .andExpect(status().isOk());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
        WaitList testWaitList = waitListList.get(waitListList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateWaitListWithPatch() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();

        // Update the waitList using partial update
        WaitList partialUpdatedWaitList = new WaitList();
        partialUpdatedWaitList.setId(waitList.getId());

        restWaitListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWaitList))
            )
            .andExpect(status().isOk());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
        WaitList testWaitList = waitListList.get(waitListList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, waitList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(waitList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(waitList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWaitList() throws Exception {
        int databaseSizeBeforeUpdate = waitListRepository.findAll().size();
        waitList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(waitList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitList in the database
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWaitList() throws Exception {
        // Initialize the database
        waitListRepository.saveAndFlush(waitList);

        int databaseSizeBeforeDelete = waitListRepository.findAll().size();

        // Delete the waitList
        restWaitListMockMvc
            .perform(delete(ENTITY_API_URL_ID, waitList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WaitList> waitListList = waitListRepository.findAll();
        assertThat(waitListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
