package com.justdoit.watchdog.repository;

import com.github.dockerjava.api.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justdoit.watchdog.model.Containers;

import java.util.Optional;

@Repository
public interface ContainerRepository extends JpaRepository<Containers, Integer>{
    Optional<Container> findByDockerId(String dockerId);
}