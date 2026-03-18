package de.iu.ipwa.ghostnet.service;

public class ReportNetCommand {

    private final Double latitude;
    private final Double longitude;
    private final Double estimatedSizeM2;
    private final boolean anonymousReport;
    private final String reporterName;
    private final String reporterPhone;

    public ReportNetCommand(
            Double latitude,
            Double longitude,
            Double estimatedSizeM2,
            boolean anonymousReport,
            String reporterName,
            String reporterPhone
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSizeM2 = estimatedSizeM2;
        this.anonymousReport = anonymousReport;
        this.reporterName = reporterName;
        this.reporterPhone = reporterPhone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getEstimatedSizeM2() {
        return estimatedSizeM2;
    }

    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }
}
