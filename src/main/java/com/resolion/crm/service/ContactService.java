package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.ContactLeadSource;
import com.resolion.crm.dto.ContactRequest;
import com.resolion.crm.dto.ContactResponse;
import com.resolion.crm.entity.ContactsEntity;
import com.resolion.crm.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public ContactResponse createContact(String email, Long companyId, ContactRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        ContactsEntity contact = ContactsEntity.builder()
                .salutation(request.getSalutation())
                .ownerName(request.getOwnerName())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .leadSource(request.getLeadSource())
                .accountName(request.getAccountName())
                .title(request.getTitle())
                .email(request.getEmail())
                .department(request.getDepartment())
                .phone(request.getPhone())
                .homePhone(request.getHomePhone())
                .otherPhone(request.getOtherPhone())
                .fax(request.getFax())
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .assistant(request.getAssistant())
                .assistantPhone(request.getAssistantPhone())
                .emailOptOut(request.getEmailOptOut())
                .skypeId(request.getSkypeId())
                .secondaryEmail(request.getSecondaryEmail())
                .x(request.getX())
                .instagram(request.getInstagram())
                .facebook(request.getFacebook())
                .linkedIn(request.getLinkedIn())
                .reportingTo(request.getReportingTo())
                .mailingStreet(request.getMailingStreet())
                .otherStreet(request.getOtherStreet())
                .mailingCity(request.getMailingCity())
                .otherCity(request.getOtherCity())
                .mailingState(request.getMailingState())
                .otherState(request.getOtherState())
                .mailingZip(request.getMailingZip())
                .otherZip(request.getOtherZip())
                .mailingCountry(request.getMailingCountry())
                .otherCountry(request.getOtherCountry())
                .description(request.getDescription())
                .copyAddress(request.getCopyAddress())
                .companyId(companyId)
                .createdBy(email)
                .build();

        ContactsEntity saved = contactsRepository.save(contact);
        return toResponse(saved);
    }

    public List<ContactResponse> getVisibleContacts(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<ContactsEntity> contacts;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            contacts = contactsRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            contacts = contactsRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            contacts = contactsRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return contacts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ContactResponse getContactById(String email, Long id) {
        ContactsEntity contact = contactsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        validateAccess(email, contact.getCompanyId());

        return toResponse(contact);
    }

    public ContactResponse updateContact(String email, Long id, ContactRequest request) {
        ContactsEntity contact = contactsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        validateAccess(email, contact.getCompanyId());
        validateRequest(request);

        contact.setSalutation(request.getSalutation());
        contact.setOwnerName(request.getOwnerName());
        contact.setOwnerEmail(request.getOwnerEmail());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setLeadSource(request.getLeadSource());
        contact.setAccountName(request.getAccountName());
        contact.setTitle(request.getTitle());
        contact.setEmail(request.getEmail());
        contact.setDepartment(request.getDepartment());
        contact.setPhone(request.getPhone());
        contact.setHomePhone(request.getHomePhone());
        contact.setOtherPhone(request.getOtherPhone());
        contact.setFax(request.getFax());
        contact.setMobile(request.getMobile());
        contact.setDateOfBirth(request.getDateOfBirth());
        contact.setAssistant(request.getAssistant());
        contact.setAssistantPhone(request.getAssistantPhone());
        contact.setEmailOptOut(request.getEmailOptOut());
        contact.setSkypeId(request.getSkypeId());
        contact.setSecondaryEmail(request.getSecondaryEmail());
        contact.setX(request.getX());
        contact.setInstagram(request.getInstagram());
        contact.setFacebook(request.getFacebook());
        contact.setLinkedIn(request.getLinkedIn());
        contact.setReportingTo(request.getReportingTo());
        contact.setMailingStreet(request.getMailingStreet());
        contact.setOtherStreet(request.getOtherStreet());
        contact.setMailingCity(request.getMailingCity());
        contact.setOtherCity(request.getOtherCity());
        contact.setMailingState(request.getMailingState());
        contact.setOtherState(request.getOtherState());
        contact.setMailingZip(request.getMailingZip());
        contact.setOtherZip(request.getOtherZip());
        contact.setMailingCountry(request.getMailingCountry());
        contact.setOtherCountry(request.getOtherCountry());
        contact.setDescription(request.getDescription());
        contact.setCopyAddress(request.getCopyAddress());

        ContactsEntity saved = contactsRepository.save(contact);
        return toResponse(saved);
    }

    public void deleteContact(String email, Long id) {
        ContactsEntity contact = contactsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        validateAccess(email, contact.getCompanyId());

        contactsRepository.delete(contact);
    }

    public List<ContactResponse> searchContacts(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return contactsRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ContactResponse> getContactsByLeadSource(String email,
                                                         Long companyId,
                                                         ContactLeadSource leadSource) {
        validateAccess(email, companyId);

        return contactsRepository.findByCompanyIdAndLeadSourceOrderByIdDesc(companyId, leadSource)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countContacts(String email, Long companyId) {
        validateAccess(email, companyId);
        return contactsRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(ContactRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new RuntimeException("First name is required");
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new RuntimeException("Last name is required");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().contains("@")) {
            throw new RuntimeException("Invalid email");
        }
    }

    private ContactResponse toResponse(ContactsEntity contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .salutation(contact.getSalutation())
                .ownerName(contact.getOwnerName())
                .ownerEmail(contact.getOwnerEmail())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .fullName(contact.getFullName())
                .leadSource(contact.getLeadSource())
                .accountName(contact.getAccountName())
                .title(contact.getTitle())
                .email(contact.getEmail())
                .department(contact.getDepartment())
                .phone(contact.getPhone())
                .homePhone(contact.getHomePhone())
                .otherPhone(contact.getOtherPhone())
                .fax(contact.getFax())
                .mobile(contact.getMobile())
                .dateOfBirth(contact.getDateOfBirth())
                .assistant(contact.getAssistant())
                .assistantPhone(contact.getAssistantPhone())
                .emailOptOut(contact.getEmailOptOut())
                .skypeId(contact.getSkypeId())
                .secondaryEmail(contact.getSecondaryEmail())
                .x(contact.getX())
                .instagram(contact.getInstagram())
                .facebook(contact.getFacebook())
                .linkedIn(contact.getLinkedIn())
                .reportingTo(contact.getReportingTo())
                .mailingStreet(contact.getMailingStreet())
                .otherStreet(contact.getOtherStreet())
                .mailingCity(contact.getMailingCity())
                .otherCity(contact.getOtherCity())
                .mailingState(contact.getMailingState())
                .otherState(contact.getOtherState())
                .mailingZip(contact.getMailingZip())
                .otherZip(contact.getOtherZip())
                .mailingCountry(contact.getMailingCountry())
                .otherCountry(contact.getOtherCountry())
                .description(contact.getDescription())
                .copyAddress(contact.getCopyAddress())
                .companyId(contact.getCompanyId())
                .createdBy(contact.getCreatedBy())
                .createdDate(contact.getCreatedDate())
                .updatedDate(contact.getUpdatedDate())
                .build();
    }
}