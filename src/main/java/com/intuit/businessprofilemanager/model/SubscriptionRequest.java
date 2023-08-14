package com.intuit.businessprofilemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {
    @NotNull
    private BusinessProfile profile;
    @NotNull
    @Size(min = 1, max = 100)  //assumption on max products
    private List<String> products;
}
