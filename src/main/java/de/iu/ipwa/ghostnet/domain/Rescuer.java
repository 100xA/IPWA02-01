package de.iu.ipwa.ghostnet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rescuer")
public class Rescuer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "phone", nullable = false, unique = true, length = 30)
    private String phone;

    @OneToMany(mappedBy = "assignedRescuer", fetch = FetchType.LAZY)
    private List<GhostNet> assignedNets = new ArrayList<>();

    public Rescuer() {
    }

    public Rescuer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<GhostNet> getAssignedNets() {
        return assignedNets;
    }

    public void setAssignedNets(List<GhostNet> assignedNets) {
        this.assignedNets = assignedNets;
    }
}
