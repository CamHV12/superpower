package com.company.enterprise.auth.repository;

import com.company.enterprise.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            select u from User u
             where lower(u.email) like lower(concat('%', :keyword, '%'))
                or lower(u.firstName) like lower(concat('%', :keyword, '%'))
                or lower(u.lastName) like lower(concat('%', :keyword, '%'))
            """)
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
            update User u
               set u.failedLoginAttempts = u.failedLoginAttempts + 1,
                   u.lockedUntil = case
                       when u.failedLoginAttempts + 1 >= :maxAttempts then :lockedUntil
                       else u.lockedUntil
                   end
             where lower(u.email) = lower(:email)
               and (u.lockedUntil is null or u.lockedUntil <= :now)
            """)
    int recordFailedLogin(@Param("email") String email,
                          @Param("maxAttempts") int maxAttempts,
                          @Param("lockedUntil") Instant lockedUntil,
                          @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("""
            update User u
               set u.failedLoginAttempts = 0,
                   u.lockedUntil = null
             where lower(u.email) = lower(:email)
            """)
    int resetLoginFailures(@Param("email") String email);
}
