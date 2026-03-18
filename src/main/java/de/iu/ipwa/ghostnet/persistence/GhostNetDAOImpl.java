package de.iu.ipwa.ghostnet.persistence;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import de.iu.ipwa.ghostnet.domain.GhostNetStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class GhostNetDAOImpl implements GhostNetDAO {

    @Override
    public GhostNet findById(EntityManager em, long id) {
        return em.find(GhostNet.class, id);
    }

    @Override
    public List<GhostNet> findOpen(EntityManager em) {
        return em.createQuery(
                        "SELECT g FROM GhostNet g "
                                + "LEFT JOIN FETCH g.assignedRescuer r "
                                + "WHERE g.status IN :statuses "
                                + "ORDER BY g.reportedAt DESC",
                        GhostNet.class
                )
                .setParameter(
                        "statuses",
                        List.of(GhostNetStatus.GEMELDET, GhostNetStatus.BERGUNG_BEVORSTEHEND)
                )
                .getResultList();
    }

    @Override
    public List<GhostNet> findAssignedByPhone(EntityManager em, String phone) {
        return em.createQuery(
                        "SELECT g FROM GhostNet g "
                                + "JOIN FETCH g.assignedRescuer r "
                                + "WHERE r.phone = :phone "
                                + "AND g.status = :status "
                                + "ORDER BY g.rescueAnnouncedAt DESC",
                        GhostNet.class
                )
                .setParameter("phone", phone)
                .setParameter("status", GhostNetStatus.BERGUNG_BEVORSTEHEND)
                .getResultList();
    }

    @Override
    public void persist(EntityManager em, GhostNet net) {
        em.persist(net);
    }

    @Override
    public GhostNet merge(EntityManager em, GhostNet net) {
        return em.merge(net);
    }
}
