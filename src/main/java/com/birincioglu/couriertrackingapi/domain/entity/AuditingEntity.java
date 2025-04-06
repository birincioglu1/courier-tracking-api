
package com.birincioglu.couriertrackingapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@EntityListeners({ AuditingEntityListener.class })
public abstract class AuditingEntity extends BaseEntity {

    private static final long serialVersionUID = 4486273975181424601L;

    @CreatedDate
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditingEntity)) {
            return false;
        }
        AuditingEntity that = (AuditingEntity) o;
        return (
                Objects.equals(createdAt, that.createdAt) &&
                        Objects.equals(updatedAt, that.updatedAt) &&
                        Objects.equals(isDeleted, that.isDeleted)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdAt, updatedAt, isDeleted);
    }
}

