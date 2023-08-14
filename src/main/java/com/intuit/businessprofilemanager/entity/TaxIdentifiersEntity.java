package com.intuit.businessprofilemanager.entity;

import com.intuit.businessprofilemanager.model.TaxIdentifierType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "tax_identifiers")
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "tax_identifiers")
public class TaxIdentifiersEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tax_identifier")
    @Enumerated(EnumType.STRING)
    private TaxIdentifierType taxIdentifierType;

    @Column(name = "tax_identifier_no")
    private String taxIdentifierNo;

    @ManyToOne
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Override
    public String toString() {
        return "TaxIdentifiersEntity{" +
                "id=" + id +
                ", taxIdentifierType=" + taxIdentifierType +
                ", taxIdentifierNo='" + taxIdentifierNo + '\'' +
                ", profile=" + profile +
                '}';
    }
}
