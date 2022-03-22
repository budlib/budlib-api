package com.budlib.api.repository;

import com.budlib.api.model.TrnQuantities;
import com.budlib.api.model.TrnQuantitiesId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrnQuantitiesRepository extends CrudRepository<TrnQuantities, TrnQuantitiesId> {

}
