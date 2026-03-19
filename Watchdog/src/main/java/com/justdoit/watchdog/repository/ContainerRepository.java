package com.justdoit.watchdog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justdoit.watchdog.model.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Integer>{}