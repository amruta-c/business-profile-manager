package com.intuit.businessprofilemanager.entity;

import com.intuit.businessprofilemanager.model.AddressType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "address")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
public class AddressEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;

    @Column(name = "address_line_1")
    private String line1;

    @Column(name = "address_line_2")
    private String line2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "country")
    private String country;

    @OneToOne(mappedBy = "legalAddress")
    private ProfileEntity profilesWithLegalAddress;

    @OneToOne(mappedBy = "businessAddress")
    private ProfileEntity profilesWithBusinessAddress;

}
