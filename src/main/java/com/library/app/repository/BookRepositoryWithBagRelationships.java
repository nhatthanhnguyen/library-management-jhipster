package com.library.app.repository;

import com.library.app.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookRepositoryWithBagRelationships {
    Optional<Book> fetchBagRelationships(Optional<Book> book);

    List<Book> fetchBagRelationships(List<Book> books);

    Page<Book> fetchBagRelationships(Page<Book> books);
}
