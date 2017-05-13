package vaadin.back.repository;

import org.springframework.data.repository.CrudRepository;
import vaadin.back.entity.Hotel;

import java.util.List;

/**
 * Created by Octoplar on 10.05.2017.
 */

//@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
    List<Hotel> findByNameLikeIgnoreCase(String filter);
    List<Hotel> findByAddressLikeIgnoreCase(String filter);
}
