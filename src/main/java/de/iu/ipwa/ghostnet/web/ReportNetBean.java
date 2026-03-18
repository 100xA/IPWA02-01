package de.iu.ipwa.ghostnet.web;

import de.iu.ipwa.ghostnet.service.GhostNetService;
import de.iu.ipwa.ghostnet.service.ReportNetCommand;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class ReportNetBean implements Serializable {

    private Double latitude;
    private Double longitude;
    private Double estimatedSizeM2;
    private boolean anonymousReport = true;
    private String reporterName;
    private String reporterPhone;

    @Inject
    private GhostNetService ghostNetService;

    public String submit() {
        try {
            ReportNetCommand command = new ReportNetCommand(
                latitude,
                longitude,
                estimatedSizeM2,
                anonymousReport,
                reporterName,
                reporterPhone
            );
            ghostNetService.reportNet(command);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, UiMessages.info("message.report.success"));
            context.getExternalContext().getFlash().setKeepMessages(true);
            clearForm();
            return "index?faces-redirect=true";
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                UiMessages.errorDetail(
                    UiMessages.resolveDetailMessage(e, "error.unexpected.save")
                )
            );
            return null;
        }
    }

    public void onAnonymousToggle() {
        if (anonymousReport) {
            reporterName = null;
            reporterPhone = null;
        }
    }

    private void clearForm() {
        latitude = null;
        longitude = null;
        estimatedSizeM2 = null;
        anonymousReport = true;
        reporterName = null;
        reporterPhone = null;
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
}
