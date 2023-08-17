package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.exception.DataNotFoundException;
import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.exception.RepositoryException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.repository.BusinessProfileRepository;
import com.intuit.businessprofilemanager.utils.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.intuit.businessprofilemanager.utils.TestConstants.*;
import static com.intuit.businessprofilemanager.utils.TestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessProfileServiceTest {

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private BusinessProfileRepository repository;

    @InjectMocks
    private BusinessProfileService service;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProfileEntity mockedProfileEntity;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BusinessProfileUpdateRequest mockedBusinessProfileUpdateRequest;

    @Test
    void testCreateProfileForPositiveCase() {
        BusinessProfile profile = getBusinessProfile();
        List<String> products = List.of(PAYROLL, PAYMENT);
        when(repository.saveAndFlush(any(ProfileEntity.class))).thenReturn(ProfileEntity.builder().id(ID).build());

        Long profileId = service.createProfile(profile, products);

        assertEquals(ID, profileId);
        verify(repository).saveAndFlush(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testCreateProfileWhenExceptionIsThrown() {
        BusinessProfile profile = getBusinessProfile();
        List<String> products = List.of(PAYROLL, PAYMENT);
        when(repository.saveAndFlush(any(ProfileEntity.class))).thenThrow(new PersistenceException(ERROR_MESSAGE));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> service.createProfile(profile, products),
                "Expect createProfile() to throw RepositoryException but it didn't");
        assertEquals("Failure in persisting profile details", exception.getMessage());
        verify(repository).saveAndFlush(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testGetProfileForPositiveCase() {
        when(repository.getReferenceById(ID)).thenReturn(mockedProfileEntity);
        when(mockedProfileEntity.getSubscriptionEntities()).thenReturn(Set.of(SubscriptionEntity.builder()
                .product(PAYROLL).build()));

        BusinessProfileData profile = service.getProfile(ID);

        assertEquals(ID, profile.getProfile().getId());
        verify(repository).getReferenceById(ID);
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testGetProfileWhenInvalidProfileIdIsPassed() {
        when(repository.getReferenceById(ID)).thenThrow(new EntityNotFoundException(ERROR_MESSAGE));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> service.getProfile(ID),
                "Expect getProfile() to throw DataNotFoundException but it didn't");
        String expectedMessage = String.format("The given profileId: %s doesn't exist or profileId is invalid", ID);
        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).getReferenceById(ID);
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testGetProfileWhenPersistenceExceptionIsThrown() {
        when(repository.getReferenceById(ID)).thenThrow(new PersistenceException(ERROR_MESSAGE));

        RepositoryException exception = assertThrows(RepositoryException.class, () -> service.getProfile(ID),
                "Expect getProfile() to throw RepositoryException but it didn't");
        assertThat(exception.getMessage()).contains("An error occurred while attempting to read profile for profileId")
                .contains(String.valueOf(ID));
        verify(repository).getReferenceById(ID);
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testUpdateProfile() {
        when(repository.getReferenceById(ID)).thenReturn(mockedProfileEntity);
        when(mockedProfileEntity.getSubscriptionEntities()).thenReturn(Set.of(SubscriptionEntity.builder()
                .product(PAYROLL).build()));
        when(repository.save(any(ProfileEntity.class))).thenReturn(mockedProfileEntity);

        BusinessProfileData businessProfileData = service.updateProfile(ID, mockedBusinessProfileUpdateRequest);

        assertNotNull(businessProfileData);
        verify(repository).getReferenceById(ID);
        verify(repository).save(any(ProfileEntity.class));
        verify(validationUtil).validateProfileWithProducts(any(BusinessProfile.class), anyList());
    }

    @Test
    void testUpdateProfileWhenValidationFails() {
        when(repository.getReferenceById(ID)).thenReturn(mockedProfileEntity);
        when(mockedProfileEntity.getSubscriptionEntities()).thenReturn(Set.of(SubscriptionEntity.builder()
                .product(PAYROLL).build()));
        List<ValidationResponse> validationResponses = getValidationResponses(ValidationStatus.FAILED, 2);
        doThrow(new DataValidationException(validationResponses))
                .when(validationUtil).validateProfileWithProducts(any(BusinessProfile.class), anyList());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.updateProfile(ID, mockedBusinessProfileUpdateRequest),
                "Expect updateProfile() to throw DataValidationException but it didn't");

        assertEquals(validationResponses, exception.getFailedValidationResponses());
        verify(repository).getReferenceById(ID);
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verify(validationUtil).validateProfileWithProducts(any(BusinessProfile.class), anyList());
    }

    @Test
    void testUpdateProfileForNonExistingProfile() {
        when(repository.getReferenceById(ID)).thenThrow(new EntityNotFoundException(ERROR_MESSAGE));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> service.updateProfile(ID, mockedBusinessProfileUpdateRequest),
                "Expect updateProfile() to throw DataNotFoundException but it didn't");

        String expectedMessage = String.format("The given profileId: %s doesn't exist or profileId is invalid", ID);

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).getReferenceById(ID);
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testUpdateProfileWhenPersistenceExceptionIsThrown() {
        when(repository.getReferenceById(ID)).thenThrow(new PersistenceException(ERROR_MESSAGE));

        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> service.updateProfile(ID, mockedBusinessProfileUpdateRequest),
                "Expect updateProfile() to throw RepositoryException but it didn't");

        String expectedMessage = String.format("An error occurred while attempting to update profile for profileId: %s.",
                ID);
        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).getReferenceById(ID);
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testDeleteProfile() {
        doNothing().when(repository).deleteById(ID);
        assertTrue(service.deleteProfile(ID));
        verify(repository).deleteById(ID);
    }

    @Test
    void testDeleteProfileWhenPersistenceExceptionIsThrown() {
        doThrow(new PersistenceException(ERROR_MESSAGE)).when(repository).deleteById(ID);
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> service.deleteProfile(ID), "Expect deleteProfile() to throw RepositoryException but it didn't");

        String expectedMessage = String.format("An error occurred while attempting to delete profile for profileId: %s.",
                ID);
        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).deleteById(ID);
    }

    @Test
    void testUpdateSubscription() {
        List<String> products = new ArrayList<>();
        products.add(PAYROLL);
        when(repository.getReferenceById(ID)).thenReturn(getProfileEntityWithSubscriptions(ID, PAYMENT));
        when(repository.save(any(ProfileEntity.class))).thenReturn(mockedProfileEntity);

        BusinessProfile profile = service.updateSubscription(ID, products);

        assertNotNull(profile);
        verify(repository).getReferenceById(ID);
        verify(repository).save(any(ProfileEntity.class));
        verify(validationUtil).validateProfileWithProducts(any(BusinessProfile.class), anyList());
    }

    @Test
    void testUpdateSubscriptionWhenNoNewProductsToSubscribe() {
        List<String> products = new ArrayList<>();
        products.add(PAYROLL);
        when(repository.getReferenceById(ID)).thenReturn(getProfileEntityWithSubscriptions(ID, PAYROLL));

        BusinessProfile profile = service.updateSubscription(ID, products);

        assertNotNull(profile);
        verify(repository).getReferenceById(ID);
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testUpdateSubscriptionForInvalidProfileId() {
        when(repository.getReferenceById(ID)).thenThrow(new EntityNotFoundException(ERROR_MESSAGE));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> service.updateSubscription(ID, List.of(PAYROLL)),
                "Expect updateSubscription() to throw DataNotFoundException but it didn't");

        String expectedMessage = String.format("The given profileId: %s doesn't exist or profileId is invalid", ID);
        assertEquals(expectedMessage, exception.getMessage());
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }

    @Test
    void testUpdateSubscriptionWhenPersistenceExceptionIsThrown() {
        when(repository.getReferenceById(ID)).thenThrow(new PersistenceException(ERROR_MESSAGE));

        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> service.updateSubscription(ID, List.of(PAYROLL)),
                "Expect updateSubscription() to throw RepositoryException but it didn't");

        assertThat(exception.getMessage())
                .contains("An error occurred while attempting to update subscription for profileId:")
                .contains(String.valueOf(ID));
        verify(repository, times(0)).save(any(ProfileEntity.class));
        verifyNoInteractions(validationUtil);
    }
}