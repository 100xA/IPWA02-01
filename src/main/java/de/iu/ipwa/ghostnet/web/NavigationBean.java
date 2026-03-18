package de.iu.ipwa.ghostnet.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class NavigationBean {

    public String toIndex() {
        return "index?faces-redirect=true";
    }

    public String toReportNet() {
        return "report-net?faces-redirect=true";
    }

    public String toOpenNets() {
        return "open-nets?faces-redirect=true";
    }

    public String toMyRecoveries() {
        return "my-recoveries?faces-redirect=true";
    }
}
