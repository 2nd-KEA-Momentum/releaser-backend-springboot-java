package com.momentum.releaser.domain.user.dao;

import com.momentum.releaser.domain.user.domain.AuthSocial;
import com.momentum.releaser.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="auth-social", path="auth-social")

public interface AuthSocialRepository extends JpaRepository<AuthSocial, Long> {
    Optional<AuthSocial> findByUser(Optional<User> userOptional);
}
