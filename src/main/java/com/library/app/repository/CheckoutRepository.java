package com.library.app.repository;

import com.library.app.domain.Checkout;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Checkout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    @Query("select checkout from Checkout checkout where checkout.user.login = ?#{principal.username}")
    List<Checkout> findByUserIsCurrentUser();
}
