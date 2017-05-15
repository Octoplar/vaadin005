package vaadin.back.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vaadin.back.entity.Hotel;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Octoplar on 10.05.2017. *
 */

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 30)
public class HotelServiceDbImpl implements HotelService {

    @PersistenceContext(name = "demo_hotels")
    EntityManager em;


    public HotelServiceDbImpl() {
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAll() {
        Query q = em.createQuery("select e from Hotel as e");
        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAllNameFilter(String filter) {
        if (filter == null) {
            return this.findAll();
        }
        Query q=em.createNamedQuery("Hotel.byName", Hotel.class);
        q.setParameter("filter","%" + filter + "%");

        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAllAddressFilter(String filter) {
        if (filter == null) {
            return this.findAll();
        }
        Query q=em.createNamedQuery("Hotel.byAddress", Hotel.class);
        q.setParameter("filter","%" + filter + "%");

        return q.getResultList();
    }

    @Override
    public void save(Hotel hotel) {
        try{
            if(hotel.getId()==null)
                em.persist(hotel);
            else
                em.merge(hotel);
        }
        catch (EntityNotFoundException e){
            //attempting to save category that is already deleted
            throw new OptimisticLockException(e);
        }


    }

    @Override
    public void delete(Hotel hotel) {
        em.remove(em.contains(hotel) ? hotel : em.merge(hotel));
    }

    @Override
    public void deleteAll(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            em.remove(em.contains(hotel) ? hotel : em.merge(hotel));
        }
    }

    @Override
    public void saveAll(List<Hotel> hotels) {
        try{
            for (Hotel hotel : hotels) {
                if(hotel.getId()==null)
                    em.persist(hotel);
                else
                    em.merge(hotel);
            }
        }
        catch (EntityNotFoundException e){
            //attempting to save category that is already deleted
            throw new OptimisticLockException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int AllCount() {
        Query q=em.createNamedQuery("Hotel.All.Count", Long.class);
        return (int) ((Number)q.getSingleResult()).longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public int nameFilterCount(String filter) {
        Query q=em.createNamedQuery("Hotel.NameFilter.Count", Long.class);
        q.setParameter("filter","%" + filter + "%");
        return (int) ((Number)q.getSingleResult()).longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public int addressFilterCount(String filter) {
        Query q=em.createNamedQuery("Hotel.AddressFilter.Count", Long.class);
        q.setParameter("filter","%" + filter + "%");
        return (int) ((Number)q.getSingleResult()).longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAll(int fromIndex, int count) {
        Query q = em.createQuery("select e from Hotel as e");
        q.setFirstResult(fromIndex);
        q.setMaxResults(count);

        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAllNameFilter(String filter, int fromIndex, int count) {
        if (filter == null) {
            return this.findAll(fromIndex, count);
        }
        Query q=em.createNamedQuery("Hotel.byName", Hotel.class);
        q.setParameter("filter","%" + filter + "%");
        q.setFirstResult(fromIndex);
        q.setMaxResults(count);

        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> findAllAddressFilter(String filter, int fromIndex, int count) {
        if (filter == null) {
            return this.findAll(fromIndex, count);
        }
        Query q=em.createNamedQuery("Hotel.byAddress", Hotel.class);
        q.setParameter("filter","%" + filter + "%");
        q.setFirstResult(fromIndex);
        q.setMaxResults(count);

        return q.getResultList();
    }
}
