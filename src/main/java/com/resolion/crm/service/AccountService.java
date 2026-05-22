package com.resolion.crm.service;

import com.resolion.crm.enums.AccountIndustry;
import com.resolion.crm.enums.AccountRating;
import com.resolion.crm.enums.AccountType;
import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.dto.AccountRequest;
import com.resolion.crm.dto.AccountResponse;
import com.resolion.crm.entity.AccountEntity;
import com.resolion.crm.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public AccountResponse createAccount(String email, Long companyId, AccountRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        AccountEntity account = AccountEntity.builder()
                .accountOwner(request.getAccountOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .rating(request.getRating())
                .accountSite(request.getAccountSite())
                .phone(request.getPhone())
                .accountName(request.getAccountName())
                .fax(request.getFax())
                .parentAccount(request.getParentAccount())
                .website(request.getWebsite())
                .accountNumber(request.getAccountNumber())
                .tickerSymbol(request.getTickerSymbol())
                .accountType(request.getAccountType())
                .ownership(request.getOwnership())
                .industry(request.getIndustry())
                .employees(request.getEmployees())
                .annualRevenue(request.getAnnualRevenue())
                .sicCode(request.getSicCode())
                .copyAddress(request.getCopyAddress())
                .billingStreet(request.getBillingStreet())
                .shippingStreet(request.getShippingStreet())
                .billingCity(request.getBillingCity())
                .shippingCity(request.getShippingCity())
                .billingState(request.getBillingState())
                .shippingState(request.getShippingState())
                .billingCode(request.getBillingCode())
                .shippingCode(request.getShippingCode())
                .billingCountry(request.getBillingCountry())
                .shippingCountry(request.getShippingCountry())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        AccountEntity saved = accountRepository.save(account);
        return toResponse(saved);
    }

    public List<AccountResponse> getVisibleAccounts(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<AccountEntity> accounts;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            accounts = accountRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            accounts = accountRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            accounts = accountRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return accounts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(String email, Long id) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateAccess(email, account.getCompanyId());

        return toResponse(account);
    }

    public AccountResponse updateAccount(String email, Long id, AccountRequest request) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateAccess(email, account.getCompanyId());
        validateRequest(request);

        account.setAccountOwner(request.getAccountOwner());
        account.setOwnerEmail(request.getOwnerEmail());
        account.setRating(request.getRating());
        account.setAccountSite(request.getAccountSite());
        account.setPhone(request.getPhone());
        account.setAccountName(request.getAccountName());
        account.setFax(request.getFax());
        account.setParentAccount(request.getParentAccount());
        account.setWebsite(request.getWebsite());
        account.setAccountNumber(request.getAccountNumber());
        account.setTickerSymbol(request.getTickerSymbol());
        account.setAccountType(request.getAccountType());
        account.setOwnership(request.getOwnership());
        account.setIndustry(request.getIndustry());
        account.setEmployees(request.getEmployees());
        account.setAnnualRevenue(request.getAnnualRevenue());
        account.setSicCode(request.getSicCode());
        account.setCopyAddress(request.getCopyAddress());
        account.setBillingStreet(request.getBillingStreet());
        account.setShippingStreet(request.getShippingStreet());
        account.setBillingCity(request.getBillingCity());
        account.setShippingCity(request.getShippingCity());
        account.setBillingState(request.getBillingState());
        account.setShippingState(request.getShippingState());
        account.setBillingCode(request.getBillingCode());
        account.setShippingCode(request.getShippingCode());
        account.setBillingCountry(request.getBillingCountry());
        account.setShippingCountry(request.getShippingCountry());
        account.setDescription(request.getDescription());

        AccountEntity saved = accountRepository.save(account);
        return toResponse(saved);
    }

    public void deleteAccount(String email, Long id) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateAccess(email, account.getCompanyId());

        accountRepository.delete(account);
    }

    public List<AccountResponse> searchAccounts(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return accountRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getByAccountType(String email, Long companyId, AccountType accountType) {
        validateAccess(email, companyId);

        return accountRepository.findByCompanyIdAndAccountTypeOrderByIdDesc(companyId, accountType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getByIndustry(String email, Long companyId, AccountIndustry industry) {
        validateAccess(email, companyId);

        return accountRepository.findByCompanyIdAndIndustryOrderByIdDesc(companyId, industry)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getByRating(String email, Long companyId, AccountRating rating) {
        validateAccess(email, companyId);

        return accountRepository.findByCompanyIdAndRatingOrderByIdDesc(companyId, rating)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countAccounts(String email, Long companyId) {
        validateAccess(email, companyId);
        return accountRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(AccountRequest request) {
        if (request.getAccountName() == null || request.getAccountName().isBlank()) {
            throw new RuntimeException("Account name is required");
        }

        if (request.getAnnualRevenue() != null && request.getAnnualRevenue().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Annual revenue cannot be negative");
        }

        if (request.getWebsite() != null && !request.getWebsite().isBlank()) {
            String website = request.getWebsite().toLowerCase();

            if (!website.startsWith("http://") && !website.startsWith("https://")) {
                request.setWebsite("https://" + request.getWebsite());
            }
        }
    }

    private AccountResponse toResponse(AccountEntity account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountOwner(account.getAccountOwner())
                .ownerEmail(account.getOwnerEmail())
                .rating(account.getRating())
                .accountSite(account.getAccountSite())
                .phone(account.getPhone())
                .accountName(account.getAccountName())
                .fax(account.getFax())
                .parentAccount(account.getParentAccount())
                .website(account.getWebsite())
                .accountNumber(account.getAccountNumber())
                .tickerSymbol(account.getTickerSymbol())
                .accountType(account.getAccountType())
                .ownership(account.getOwnership())
                .industry(account.getIndustry())
                .employees(account.getEmployees())
                .annualRevenue(account.getAnnualRevenue())
                .sicCode(account.getSicCode())
                .copyAddress(account.getCopyAddress())
                .billingStreet(account.getBillingStreet())
                .shippingStreet(account.getShippingStreet())
                .billingCity(account.getBillingCity())
                .shippingCity(account.getShippingCity())
                .billingState(account.getBillingState())
                .shippingState(account.getShippingState())
                .billingCode(account.getBillingCode())
                .shippingCode(account.getShippingCode())
                .billingCountry(account.getBillingCountry())
                .shippingCountry(account.getShippingCountry())
                .description(account.getDescription())
                .companyId(account.getCompanyId())
                .createdBy(account.getCreatedBy())
                .createdDate(account.getCreatedDate())
                .updatedDate(account.getUpdatedDate())
                .build();
    }
}