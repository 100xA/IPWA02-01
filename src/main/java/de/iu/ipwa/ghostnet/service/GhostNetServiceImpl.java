package de.iu.ipwa.ghostnet.service;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import de.iu.ipwa.ghostnet.domain.GhostNetStatus;
import de.iu.ipwa.ghostnet.domain.Rescuer;
import de.iu.ipwa.ghostnet.persistence.GhostNetDAO;
import de.iu.ipwa.ghostnet.persistence.JpaFactory;
import de.iu.ipwa.ghostnet.persistence.RescuerDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@ApplicationScoped
public class GhostNetServiceImpl implements GhostNetService {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9()\\-\\s./]{6,30}$"
    );

    @Inject
    private GhostNetDAO ghostNetDAO;

    @Inject
    private RescuerDAO rescuerDAO;

    @Override
    public GhostNet reportNet(ReportNetCommand cmd) {
        validateReportCommand(cmd);

        return inTransaction(em -> {
            GhostNet net = new GhostNet();
            net.setLatitude(cmd.getLatitude());
            net.setLongitude(cmd.getLongitude());
            net.setEstimatedSizeM2(cmd.getEstimatedSizeM2());
            net.setStatus(GhostNetStatus.GEMELDET);
            net.setAnonymousReport(cmd.isAnonymousReport());
            net.setReportedAt(LocalDateTime.now());
            net.setVersion(0L);

            if (cmd.isAnonymousReport()) {
                net.setReporterName(null);
                net.setReporterPhone(null);
            } else {
                net.setReporterName(cmd.getReporterName().trim());
                net.setReporterPhone(normalizePhone(cmd.getReporterPhone()));
            }

            ghostNetDAO.persist(em, net);
            return net;
        });
    }

    @Override
    public List<GhostNet> listOpenNets() {
        EntityManager em = JpaFactory.createEntityManager();
        try {
            return ghostNetDAO.findOpen(em);
        } finally {
            em.close();
        }
    }

    @Override
    public GhostNet claimNet(long netId, RescuerIdentity identity) {
        validateRescuerIdentity(identity);

        try {
            return inTransaction(em -> {
                GhostNet net = ghostNetDAO.findById(em, netId);
                if (net == null) {
                    throw new DomainException("error.net.notFound");
                }
                if (net.getStatus() != GhostNetStatus.GEMELDET) {
                    throw new DomainException("error.net.notAvailable");
                }
                if (net.getAssignedRescuer() != null) {
                    throw new DomainException("error.net.alreadyAssigned");
                }

                String normalizedPhone = normalizePhone(identity.getPhone());
                String trimmedName = identity.getName().trim();

                Rescuer rescuer = rescuerDAO.findByPhone(em, normalizedPhone);
                if (rescuer == null) {
                    rescuer = new Rescuer(trimmedName, normalizedPhone);
                    rescuerDAO.persist(em, rescuer);
                } else if (!trimmedName.equals(rescuer.getName())) {
                    rescuer.setName(trimmedName);
                    rescuer = rescuerDAO.merge(em, rescuer);
                }

                net.setAssignedRescuer(rescuer);
                net.setStatus(GhostNetStatus.BERGUNG_BEVORSTEHEND);
                net.setRescueAnnouncedAt(LocalDateTime.now());
                return ghostNetDAO.merge(em, net);
            });
        } catch (OptimisticLockException e) {
            throw new DomainException("error.net.optimisticLock", e);
        }
    }

    @Override
    public List<GhostNet> listAssignedNets(String rescuerPhone) {
        String normalizedPhone = normalizePhone(rescuerPhone);
        EntityManager em = JpaFactory.createEntityManager();
        try {
            return ghostNetDAO.findAssignedByPhone(em, normalizedPhone);
        } finally {
            em.close();
        }
    }

    @Override
    public GhostNet markRecovered(long netId, String rescuerPhone) {
        String normalizedPhone = normalizePhone(rescuerPhone);

        try {
            return inTransaction(em -> {
                GhostNet net = ghostNetDAO.findById(em, netId);
                if (net == null) {
                    throw new DomainException("error.net.notFound");
                }
                if (net.getStatus() != GhostNetStatus.BERGUNG_BEVORSTEHEND) {
                    throw new DomainException("error.net.invalidRecoverState");
                }
                if (net.getAssignedRescuer() == null) {
                    throw new DomainException("error.net.noAssignedRescuer");
                }
                if (
                    !normalizedPhone.equals(net.getAssignedRescuer().getPhone())
                ) {
                    throw new DomainException(
                        "error.net.onlyAssignedPhoneCanRecover"
                    );
                }

                net.setStatus(GhostNetStatus.GEBORGEN);
                net.setRecoveredAt(LocalDateTime.now());
                return ghostNetDAO.merge(em, net);
            });
        } catch (OptimisticLockException e) {
            throw new DomainException("error.net.optimisticLock", e);
        }
    }

    private void validateReportCommand(ReportNetCommand cmd) {
        if (cmd == null) {
            throw new DomainException("error.report.missingData");
        }
        validateCoordinates(cmd.getLatitude(), cmd.getLongitude());
        validateSize(cmd.getEstimatedSizeM2());
        if (!cmd.isAnonymousReport()) {
            if (isBlank(cmd.getReporterName())) {
                throw new DomainException("error.reporter.nameRequired");
            }
            validatePhone(cmd.getReporterPhone(), "error.reporter.phoneInvalid");
        }
    }

    private void validateRescuerIdentity(RescuerIdentity identity) {
        if (identity == null) {
            throw new DomainException("error.rescuer.identityMissing");
        }
        if (isBlank(identity.getName())) {
            throw new DomainException("error.rescuer.nameRequired");
        }
        validatePhone(identity.getPhone(), "error.rescuer.phoneInvalid");
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new DomainException("error.coordinates.required");
        }
        if (latitude < -90.0 || latitude > 90.0) {
            throw new DomainException("error.coordinates.latitudeRange");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new DomainException("error.coordinates.longitudeRange");
        }
    }

    private void validateSize(Double estimatedSizeM2) {
        if (estimatedSizeM2 == null || estimatedSizeM2 <= 0.0) {
            throw new DomainException("error.size.invalid");
        }
    }

    private void validatePhone(String phone, String message) {
        if (isBlank(phone) || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new DomainException(message);
        }
    }

    private String normalizePhone(String phone) {
        validatePhone(phone, "error.phone.invalid");
        return phone.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private <T> T inTransaction(Function<EntityManager, T> action) {
        EntityManager em = JpaFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
