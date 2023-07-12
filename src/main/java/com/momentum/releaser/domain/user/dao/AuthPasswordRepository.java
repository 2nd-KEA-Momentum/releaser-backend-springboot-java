package com.momentum.releaser.domain.user.dao;

import com.momentum.releaser.domain.user.domain.AuthPassword;
import com.momentum.releaser.domain.user.domain.RefreshToken;
import com.momentum.releaser.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="auth-password", path="auth-password")
public interface AuthPasswordRepository extends JpaRepository<AuthPassword, Long> {
    AuthPassword findByUser(User user);
}
