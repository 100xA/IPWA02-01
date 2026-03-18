package de.iu.ipwa.ghostnet.persistence;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import jakarta.persistence.EntityManager;
import java.util.List;

public interface GhostNetDAO {

    GhostNet findById(EntityManager em, long id);

    List<GhostNet> findOpen(EntityManager em);

    List<GhostNet> findAssignedByPhone(EntityManager em, String phone);

    void persist(EntityManager em, GhostNet net);

    GhostNet merge(EntityManager em, GhostNet net);
}
