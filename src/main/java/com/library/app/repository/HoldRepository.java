package com.library.app.repository;

import com.library.app.domain.Hold;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hold entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {
    @Query("select hold from Hold hold where hold.user.login = ?#{principal.username}")
    List<Hold> findByUserIsCurrentUser();

    @Query(
        value = "select h from Hold h where h.user.login = :currentUser",
        countQuery = "select count(h) from Hold h where h.user.login = :currentUser"
    )
    Page<Hold> findHoldsByCurrentUser(@Param("currentUser") String currentUser, Pageable pageable);
}
