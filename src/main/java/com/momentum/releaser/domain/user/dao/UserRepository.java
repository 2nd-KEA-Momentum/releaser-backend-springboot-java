package com.momentum.releaser.domain.user.dao;

import com.momentum.releaser.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="user", path="user")
public interface UserRepository extends JpaRepository<User, Long> {
}
