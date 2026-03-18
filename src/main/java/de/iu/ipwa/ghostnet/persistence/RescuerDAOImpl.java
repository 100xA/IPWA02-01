package de.iu.ipwa.ghostnet.persistence;

import de.iu.ipwa.ghostnet.domain.Rescuer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class RescuerDAOImpl implements RescuerDAO {

    @Override
    public Rescuer findByPhone(EntityManager em, String phone) {
        List<Rescuer> results = em.createQuery(
                        "SELECT r FROM Rescuer r WHERE r.phone = :phone",
                        Rescuer.class
                )
                .setParameter("phone", phone)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void persist(EntityManager em, Rescuer rescuer) {
        em.persist(rescuer);
    }

    @Override
    public Rescuer merge(EntityManager em, Rescuer rescuer) {
        return em.merge(rescuer);
    }
}
