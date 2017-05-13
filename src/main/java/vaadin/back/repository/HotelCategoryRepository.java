package vaadin.back.repository;

import org.springframework.data.repository.CrudRepository;
import vaadin.back.entity.HotelCategory;

/**
 * Created by Octoplar on 10.05.2017.
 */

//@Repository
public interface HotelCategoryRepository extends CrudRepository<HotelCategory, Integer> {}
