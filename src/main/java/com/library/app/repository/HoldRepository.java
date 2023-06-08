package com.library.app.repository;

import com.library.app.domain.Hold;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hold entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {
    @Query("select hold from Hold hold where hold.user.login = ?#{principal.username}")
    List<Hold> findByUserIsCurrentUser();
}
