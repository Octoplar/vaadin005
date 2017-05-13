package vaadin.back.service;

import org.springframework.stereotype.Service;
import vaadin.back.entity.HotelCategory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by Octoplar on 10.05.2017.
 */
@Service
@Transactional
public class HotelCategoryServiceDbImpl implements HotelCategoryService{

    @PersistenceContext(name = "demo_hotels")
    EntityManager em;

    //special null value to display
    private final HotelCategory defaultPrototype=new HotelCategory("Undefined");

    @Override
    @Transactional(readOnly = true)
    public Iterable<HotelCategory> findAll() {

        Query q = em.createQuery("from HotelCategory");
        return q.getResultList();
    }

    @Override
    public void save(HotelCategory category) {
        if(category.getId()==null)
            em.persist(category);
        else
            em.merge(category);
    }

    @Override
    public void delete(HotelCategory category) {
        em.remove(em.contains(category) ? category : em.merge(category));
    }

    @Override
    public void deleteAll(List<HotelCategory> categoryList) {
        for (HotelCategory entity : categoryList) {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
        }
    }

    @Override
    public HotelCategory getDefaultCategory() {
        try {
            return defaultPrototype.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
