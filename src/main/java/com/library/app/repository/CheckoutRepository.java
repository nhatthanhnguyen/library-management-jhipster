package com.library.app.repository;

import com.library.app.domain.Checkout;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Checkout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    @Query("select checkout from Checkout checkout where checkout.user.login = ?#{principal.username}")
    List<Checkout> findByUserIsCurrentUser();

    @Query(
        value = "select c from Checkout c " +
        "where c.user.login = :currentUser " +
        "and c.isReturned is null or c.isReturned = :isReturned",
        countQuery = "select count(c) from Checkout c " +
        "where c.user.login = :currentUser " +
        "and c.isReturned is null or c.isReturned = :isReturned"
    )
    Page<Checkout> findCheckoutsByCurrentUser(
        @Param("currentUser") String username,
        @Param("isReturned") Boolean isReturned,
        Pageable pageable
    );

    @Query(
        value = "select c from Checkout c " + "where c.isReturned is null or c.isReturned = :isReturned",
        countQuery = "select count(c) from Checkout c " + "where c.isReturned is null or c.isReturned = :isReturned"
    )
    Page<Checkout> findCheckouts(@Param("isReturned") Boolean isReturned, Pageable pageable);
}
