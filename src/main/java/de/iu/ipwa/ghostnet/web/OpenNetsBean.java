package de.iu.ipwa.ghostnet.web;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import de.iu.ipwa.ghostnet.domain.GhostNetStatus;
import de.iu.ipwa.ghostnet.domain.Rescuer;
import de.iu.ipwa.ghostnet.service.GhostNetService;
import de.iu.ipwa.ghostnet.service.RescuerIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class OpenNetsBean implements Serializable {

    private List<GhostNet> openNets = new ArrayList<>();
    private Long selectedNetId;
    private String rescuerName;
    private String rescuerPhone;

    @Inject
    private GhostNetService ghostNetService;

    @PostConstruct
    public void init() {
        reloadOpenNets();
    }

    public String selectNet(long netId) {
        this.selectedNetId = netId;
        return null;
    }

    public String claimSelected() {
        try {
            if (selectedNetId == null) {
                addErrorMessage(UiMessages.text("error.open.selectFirst"));
                return null;
            }
            ghostNetService.claimNet(selectedNetId, new RescuerIdentity(rescuerName, rescuerPhone));
            FacesContext
                .getCurrentInstance()
                .addMessage(null, UiMessages.info("message.claim.success"));
            selectedNetId = null;
            rescuerName = null;
            rescuerPhone = null;
            reloadOpenNets();
            return null;
        } catch (RuntimeException e) {
            addErrorMessage(
                UiMessages.resolveDetailMessage(e, "error.unexpected.general")
            );
            return null;
        }
    }

    public void reloadOpenNets() {
        try {
            openNets = new ArrayList<>(ghostNetService.listOpenNets());
        } catch (RuntimeException e) {
            openNets = new ArrayList<>();
            addErrorMessage(
                UiMessages.resolveDetailMessage(
                    e,
                    "error.unexpected.openNetsLoad"
                )
            );
        }
    }

    public String getOpenNetsJson() {
        if (openNets == null) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < openNets.size(); i++) {
            GhostNet net = openNets.get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{");
            builder.append("\"id\":").append(net.getId()).append(",");
            builder.append("\"latitude\":").append(net.getLatitude()).append(",");
            builder.append("\"longitude\":").append(net.getLongitude()).append(",");
            builder.append("\"estimatedSizeM2\":").append(net.getEstimatedSizeM2()).append(",");
            builder.append("\"status\":\"").append(escapeJson(net.getStatus().name())).append("\",");
            builder.append("\"statusLabel\":\"")
                .append(escapeJson(UiMessages.text(statusKey(net.getStatus()))))
                .append("\",");
            Rescuer rescuer = net.getAssignedRescuer();
            if (rescuer != null) {
                builder.append("\"rescuerName\":\"").append(escapeJson(rescuer.getName())).append("\"");
            } else {
                builder.append("\"rescuerName\":null");
            }
            builder.append("}");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * JSF input components require a writable property during UPDATE_MODEL_VALUES.
     * This hidden field is output-only for client-side map rendering, so setter is intentionally no-op.
     */
    public void setOpenNetsJson(String ignored) {
        // no-op
    }

    private void addErrorMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, UiMessages.errorDetail(message));
        }
    }
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public List<GhostNet> getOpenNets() {
        return openNets;
    }

    public String statusKey(GhostNetStatus status) {
        return "status." + status.name();
    }

    public Long getSelectedNetId() {
        return selectedNetId;
    }

    public void setSelectedNetId(Long selectedNetId) {
        this.selectedNetId = selectedNetId;
    }

    public String getRescuerName() {
        return rescuerName;
    }

    public void setRescuerName(String rescuerName) {
        this.rescuerName = rescuerName;
    }

    public String getRescuerPhone() {
        return rescuerPhone;
    }

    public void setRescuerPhone(String rescuerPhone) {
        this.rescuerPhone = rescuerPhone;
    }
}
