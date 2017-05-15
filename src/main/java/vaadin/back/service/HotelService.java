package vaadin.back.service;

import vaadin.back.entity.Hotel;

import java.util.List;

/**
 * Created by Octoplar on 09.05.2017.
 */
public interface HotelService {
    List<Hotel> findAll();
    List<Hotel> findAllNameFilter(String filter);
    List<Hotel> findAllAddressFilter(String filter);
    void save(Hotel hotel);
    void delete(Hotel hotel);
    void deleteAll(List<Hotel> hotels);
    void saveAll(List<Hotel> hotels);

    //paging support
    int AllCount();
    int nameFilterCount(String filter);
    int addressFilterCount(String filter);
    List<Hotel> findAll(int fromIndex, int count);
    List<Hotel> findAllNameFilter(String filter, int fromIndex, int count);
    List<Hotel> findAllAddressFilter(String filter, int fromIndex, int count);

}
