package com.yuzhi.dtadmin.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AuditLog.
 */
@Entity
@Table(name = "audit_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "actor", nullable = false)
    private String actor;

    @NotNull
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "target")
    private String target;

    @Lob
    @Column(name = "details")
    private String details;

    @Column(name = "result")
    private String result;

    @NotNull
    @Column(name = "at", nullable = false)
    private Instant at;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActor() {
        return this.actor;
    }

    public AuditLog actor(String actor) {
        this.setActor(actor);
        return this;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return this.action;
    }

    public AuditLog action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return this.target;
    }

    public AuditLog target(String target) {
        this.setTarget(target);
        return this;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDetails() {
        return this.details;
    }

    public AuditLog details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getResult() {
        return this.result;
    }

    public AuditLog result(String result) {
        this.setResult(result);
        return this;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Instant getAt() {
        return this.at;
    }

    public AuditLog at(Instant at) {
        this.setAt(at);
        return this;
    }

    public void setAt(Instant at) {
        this.at = at;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLog)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLog{" +
            "id=" + getId() +
            ", actor='" + getActor() + "'" +
            ", action='" + getAction() + "'" +
            ", target='" + getTarget() + "'" +
            ", details='" + getDetails() + "'" +
            ", result='" + getResult() + "'" +
            ", at='" + getAt() + "'" +
            "}";
    }
}
