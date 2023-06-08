package com.library.app.repository;

import com.library.app.domain.BookCopy;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookCopy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {}
