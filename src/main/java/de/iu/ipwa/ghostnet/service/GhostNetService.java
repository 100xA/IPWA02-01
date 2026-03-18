package de.iu.ipwa.ghostnet.service;

import de.iu.ipwa.ghostnet.domain.GhostNet;
import java.util.List;

public interface GhostNetService {
    GhostNet reportNet(ReportNetCommand cmd);

    List<GhostNet> listOpenNets();

    GhostNet claimNet(long netId, RescuerIdentity identity);

    List<GhostNet> listAssignedNets(String rescuerPhone);

    GhostNet markRecovered(long netId, String rescuerPhone);
}
