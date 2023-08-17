package com.intuit.businessprofilemanager.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Builder
@Entity(name = "profile")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class ProfileEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "legal_name")
    private String legalName;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "legal_address_id")
    private AddressEntity legalAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "business_address_id")
    private AddressEntity businessAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Set<TaxIdentifiersEntity> taxIdentifiers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Set<SubscriptionEntity> subscriptionEntities;

}
