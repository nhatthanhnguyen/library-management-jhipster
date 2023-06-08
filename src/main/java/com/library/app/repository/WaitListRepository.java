package com.library.app.repository;

import com.library.app.domain.WaitList;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WaitList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WaitListRepository extends JpaRepository<WaitList, Long> {
    @Query("select waitList from WaitList waitList where waitList.user.login = ?#{principal.username}")
    List<WaitList> findByUserIsCurrentUser();
}
