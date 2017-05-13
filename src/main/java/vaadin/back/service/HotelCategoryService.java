package vaadin.back.service;

import vaadin.back.entity.HotelCategory;

import java.util.List;

/**
 * Created by Octoplar on 05.05.2017.
 */
public interface HotelCategoryService {
    Iterable<HotelCategory> findAll();
    void save(HotelCategory category);
    void delete(HotelCategory category);
    void deleteAll(List<HotelCategory> categoryList);
    HotelCategory getDefaultCategory();
}
