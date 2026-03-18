package de.iu.ipwa.ghostnet.web;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import de.iu.ipwa.ghostnet.domain.GhostNetStatus;
import de.iu.ipwa.ghostnet.service.GhostNetService;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class RecoveryBean implements Serializable {

    private String rescuerPhone;
    private List<GhostNet> assignedNets = new ArrayList<>();

    @Inject
    private GhostNetService ghostNetService;

    public String searchAssigned() {
        try {
            assignedNets = new ArrayList<>(
                ghostNetService.listAssignedNets(rescuerPhone)
            );
            if (assignedNets.isEmpty()) {
                FacesContext
                    .getCurrentInstance()
                    .addMessage(null, UiMessages.notice("message.recovery.noneFound"));
            }
            return null;
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                UiMessages.errorDetail(
                    UiMessages.resolveDetailMessage(
                        e,
                        "error.unexpected.general"
                    )
                )
            );
            return null;
        }
    }

    public String markRecovered(long netId) {
        try {
            ghostNetService.markRecovered(netId, rescuerPhone);
            FacesContext
                .getCurrentInstance()
                .addMessage(null, UiMessages.info("message.recovery.recovered"));
            assignedNets = new ArrayList<>(
                ghostNetService.listAssignedNets(rescuerPhone)
            );
            return null;
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                UiMessages.errorDetail(
                    UiMessages.resolveDetailMessage(
                        e,
                        "error.unexpected.general"
                    )
                )
            );
            return null;
        }
    }

    public String getRescuerPhone() {
        return rescuerPhone;
    }

    public void setRescuerPhone(String rescuerPhone) {
        this.rescuerPhone = rescuerPhone;
    }

    public List<GhostNet> getAssignedNets() {
        return assignedNets;
    }

    public String statusKey(GhostNetStatus status) {
        return "status." + status.name();
    }
}
