package de.iu.ipwa.ghostnet.persistence;

import de.iu.ipwa.ghostnet.domain.Rescuer;
import jakarta.persistence.EntityManager;

public interface RescuerDAO {

    Rescuer findByPhone(EntityManager em, String phone);

    void persist(EntityManager em, Rescuer rescuer);

    Rescuer merge(EntityManager em, Rescuer rescuer);
}
