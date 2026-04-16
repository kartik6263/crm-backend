package com.leadmatrix.crm.services;

import com.leadmatrix.crm.ENUMS.CompanyRole;
import com.leadmatrix.crm.entity.Company;
import com.leadmatrix.crm.entity.CompanyInvite;
import com.leadmatrix.crm.entity.CompanyMember;
import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.CompanyInviteRepository;
import com.leadmatrix.crm.respository.CompanyMemberRepository;
import com.leadmatrix.crm.respository.CompanyRepository;
import com.leadmatrix.crm.respository.crmRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CompanyAccessService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMemberRepository companyMemberRepository;

    @Autowired
    private CompanyInviteRepository companyInviteRepository;

    @Autowired
    private crmRespository crmRepository;

    public Long createCompanyWithOwner(databaseCRM user, String companyName) {
        Company company = new Company();
        company.setName(companyName);
        company.setEmail(user.getEmail());
        company.setPhone(user.getPhone());
        company.setStatus("ACTIVE");

        Company savedCompany = companyRepository.save(company);

        CompanyMember member = new CompanyMember();
        member.setCompanyId(savedCompany.getId());
        member.setUserId(user.getId());
        member.setRole(CompanyRole.OWNER);
        member.setActive(true);
        companyMemberRepository.save(member);

        return savedCompany.getId();
    }

    public List<Map<String, Object>> getUserCompanies(String email) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CompanyMember> memberships = companyMemberRepository.findByUserIdAndActiveTrue(user.getId());

        if (memberships.isEmpty()) {
            throw new RuntimeException("User is not linked to any company");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (CompanyMember membership : memberships) {
            Company company = companyRepository.findById(membership.getCompanyId()).orElse(null);
            if (company != null) {
                Map<String, Object> row = new HashMap<>();
                row.put("companyId", company.getId());
                row.put("companyName", company.getName());
                row.put("role", membership.getRole().name());
                result.add(row);
            }
        }

        return result;
    }

    public boolean hasCompanyAccess(String email, Long companyId) {
        databaseCRM user = crmRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return companyMemberRepository.findByCompanyIdAndUserIdAndActiveTrue(companyId, user.getId()).isPresent();
    }

    public CompanyRole getCompanyRole(String email, Long companyId) {
        databaseCRM user = crmRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CompanyMember member = companyMemberRepository.findByCompanyIdAndUserIdAndActiveTrue(companyId, user.getId())
                .orElseThrow(() -> new RuntimeException("No company access"));

        return member.getRole();
    }

    public String inviteUser(String adminEmail, Long companyId, String invitedEmail, String role) {
        CompanyRole inviterRole = getCompanyRole(adminEmail, companyId);

        if (!(inviterRole == CompanyRole.OWNER || inviterRole == CompanyRole.ADMIN)) {
            throw new RuntimeException("Only owner/admin can invite users");
        }

        CompanyInvite invite = new CompanyInvite();
        invite.setCompanyId(companyId);
        invite.setInvitedEmail(invitedEmail);
        invite.setRole(CompanyRole.valueOf(role.toUpperCase()));
        invite.setInviteToken(UUID.randomUUID().toString());
        invite.setStatus("PENDING");
        invite.setCreatedDate(LocalDate.now().toString());

        companyInviteRepository.save(invite);
        return invite.getInviteToken();
    }

    public String acceptInvite(String token, String email) {
        CompanyInvite invite = companyInviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        if (!"PENDING".equalsIgnoreCase(invite.getStatus())) {
            throw new RuntimeException("Invite is not active");
        }

        if (!invite.getInvitedEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Invite email mismatch");
        }

        databaseCRM user = crmRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<CompanyMember> existing = companyMemberRepository
                .findByCompanyIdAndUserIdAndActiveTrue(invite.getCompanyId(), user.getId());

        if (existing.isEmpty()) {
            CompanyMember member = new CompanyMember();
            member.setCompanyId(invite.getCompanyId());
            member.setUserId(user.getId());
            member.setRole(invite.getRole());
            member.setActive(true);
            companyMemberRepository.save(member);
        }

        invite.setStatus("ACCEPTED");
        companyInviteRepository.save(invite);

        return "Invite accepted successfully";
    }

    public void addUserToCompany(Long companyId, Long userId, CompanyRole role) {
        Optional<CompanyMember> existing = companyMemberRepository
                .findByCompanyIdAndUserIdAndActiveTrue(companyId, userId);

        if (existing.isPresent()) {
            throw new RuntimeException("User already exists in this company");
        }

        CompanyMember member = new CompanyMember();
        member.setCompanyId(companyId);
        member.setUserId(userId);
        member.setRole(role);
        member.setActive(true);

        companyMemberRepository.save(member);
    }
}