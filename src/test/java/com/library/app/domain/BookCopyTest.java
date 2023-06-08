package com.library.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.library.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookCopyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookCopy.class);
        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(bookCopy1.getId());
        assertThat(bookCopy1).isEqualTo(bookCopy2);
        bookCopy2.setId(2L);
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);
        bookCopy1.setId(null);
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);
    }
}
