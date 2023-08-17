package com.intuit.businessprofilemanager.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "subscriptions")
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "subscriptions")
public class SubscriptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product")
    private String product;

    @ManyToOne
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

}

