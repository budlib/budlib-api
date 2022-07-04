package com.budlib.api.repository;

import com.budlib.api.model.TrnQuantities;
import com.budlib.api.model.TrnQuantitiesId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on TrnQuantities
 */
@Repository
public interface TrnQuantitiesRepository extends JpaRepository<TrnQuantities, TrnQuantitiesId> {

}
