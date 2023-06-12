package com.library.app.repository;

import com.library.app.domain.WaitList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WaitList entity.
 */
@Repository
public interface WaitListRepository extends JpaRepository<WaitList, Long> {
    @Query("select waitList from WaitList waitList where waitList.user.login = ?#{principal.username}")
    List<WaitList> findByUserIsCurrentUser();

    default Optional<WaitList> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WaitList> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WaitList> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct waitList from WaitList waitList left join fetch waitList.user left join fetch waitList.book",
        countQuery = "select count(distinct waitList) from WaitList waitList"
    )
    Page<WaitList> findAllWithToOneRelationships(Pageable pageable);

    @Query("select w from WaitList w where w.user.id = :userId and w.book.id = :bookId")
    List<WaitList> findByBookAndUser(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query("select w from WaitList w where w.book.id = :bookId")
    List<WaitList> findByBook(@Param("bookId") Long bookId);

    @Query("select distinct waitList from WaitList waitList left join fetch waitList.user left join fetch waitList.book")
    List<WaitList> findAllWithToOneRelationships();

    @Query("select waitList from WaitList waitList left join fetch waitList.user left join fetch waitList.book where waitList.id =:id")
    Optional<WaitList> findOneWithToOneRelationships(@Param("id") Long id);
}
