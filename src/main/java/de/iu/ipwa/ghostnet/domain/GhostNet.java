package de.iu.ipwa.ghostnet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "ghost_net")
public class GhostNet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "estimated_size_m2", nullable = false)
    private Double estimatedSizeM2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private GhostNetStatus status;

    @Column(name = "anonymous_report", nullable = false)
    private boolean anonymousReport;

    @Column(name = "reporter_name", length = 120)
    private String reporterName;

    @Column(name = "reporter_phone", length = 30)
    private String reporterPhone;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "rescue_announced_at")
    private LocalDateTime rescueAnnouncedAt;

    @Column(name = "recovered_at")
    private LocalDateTime recoveredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_rescuer_id")
    private Rescuer assignedRescuer;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public GhostNet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getEstimatedSizeM2() {
        return estimatedSizeM2;
    }

    public void setEstimatedSizeM2(Double estimatedSizeM2) {
        this.estimatedSizeM2 = estimatedSizeM2;
    }

    public GhostNetStatus getStatus() {
        return status;
    }

    public void setStatus(GhostNetStatus status) {
        this.status = status;
    }

    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public void setAnonymousReport(boolean anonymousReport) {
        this.anonymousReport = anonymousReport;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getRescueAnnouncedAt() {
        return rescueAnnouncedAt;
    }

    public void setRescueAnnouncedAt(LocalDateTime rescueAnnouncedAt) {
        this.rescueAnnouncedAt = rescueAnnouncedAt;
    }

    public LocalDateTime getRecoveredAt() {
        return recoveredAt;
    }

    public void setRecoveredAt(LocalDateTime recoveredAt) {
        this.recoveredAt = recoveredAt;
    }

    public Rescuer getAssignedRescuer() {
        return assignedRescuer;
    }

    public void setAssignedRescuer(Rescuer assignedRescuer) {
        this.assignedRescuer = assignedRescuer;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
