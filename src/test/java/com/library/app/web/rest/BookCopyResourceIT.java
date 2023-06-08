package com.library.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.library.app.IntegrationTest;
import com.library.app.domain.BookCopy;
import com.library.app.repository.BookCopyRepository;
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
 * Integration tests for the {@link BookCopyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookCopyResourceIT {

    private static final Integer DEFAULT_YEAR_PUBLISHED = 1;
    private static final Integer UPDATED_YEAR_PUBLISHED = 2;

    private static final String ENTITY_API_URL = "/api/book-copies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookCopyMockMvc;

    private BookCopy bookCopy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().yearPublished(DEFAULT_YEAR_PUBLISHED);
        return bookCopy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createUpdatedEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().yearPublished(UPDATED_YEAR_PUBLISHED);
        return bookCopy;
    }

    @BeforeEach
    public void initTest() {
        bookCopy = createEntity(em);
    }

    @Test
    @Transactional
    void createBookCopy() throws Exception {
        int databaseSizeBeforeCreate = bookCopyRepository.findAll().size();
        // Create the BookCopy
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopy)))
            .andExpect(status().isCreated());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeCreate + 1);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getYearPublished()).isEqualTo(DEFAULT_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void createBookCopyWithExistingId() throws Exception {
        // Create the BookCopy with an existing ID
        bookCopy.setId(1L);

        int databaseSizeBeforeCreate = bookCopyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopy)))
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkYearPublishedIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookCopyRepository.findAll().size();
        // set the field null
        bookCopy.setYearPublished(null);

        // Create the BookCopy, which fails.

        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopy)))
            .andExpect(status().isBadRequest());

        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookCopies() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].yearPublished").value(hasItem(DEFAULT_YEAR_PUBLISHED)));
    }

    @Test
    @Transactional
    void getBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get the bookCopy
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL_ID, bookCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCopy.getId().intValue()))
            .andExpect(jsonPath("$.yearPublished").value(DEFAULT_YEAR_PUBLISHED));
    }

    @Test
    @Transactional
    void getNonExistingBookCopy() throws Exception {
        // Get the bookCopy
        restBookCopyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy
        BookCopy updatedBookCopy = bookCopyRepository.findById(bookCopy.getId()).get();
        // Disconnect from session so that the updates on updatedBookCopy are not directly saved in db
        em.detach(updatedBookCopy);
        updatedBookCopy.yearPublished(UPDATED_YEAR_PUBLISHED);

        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookCopy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getYearPublished()).isEqualTo(UPDATED_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void putNonExistingBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookCopy))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookCopy))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getYearPublished()).isEqualTo(DEFAULT_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void fullUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        partialUpdatedBookCopy.yearPublished(UPDATED_YEAR_PUBLISHED);

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getYearPublished()).isEqualTo(UPDATED_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void patchNonExistingBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookCopy))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookCopy))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bookCopy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeDelete = bookCopyRepository.findAll().size();

        // Delete the bookCopy
        restBookCopyMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookCopy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
