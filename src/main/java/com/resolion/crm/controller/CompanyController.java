package com.resolion.crm.controller;

import com.resolion.crm.dpo.AcceptInviteRequest;
import com.resolion.crm.dpo.InviteUserRequest;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyAccessService companyAccessService;

    public CompanyController(CompanyAccessService companyAccessService) {
        this.companyAccessService = companyAccessService;
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUser(@RequestBody InviteUserRequest request) {
        String token = companyAccessService.inviteUser(
                request.getAdminEmail(),
                request.getCompanyId(),
                request.getInvitedEmail(),
                request.getRole()
        );

        return ResponseEntity.ok("Invite created. Token: " + token);
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<?> acceptInvite(@RequestBody AcceptInviteRequest request) {
        return ResponseEntity.ok(
                companyAccessService.acceptInvite(request.getInviteToken(), request.getEmail())
        );
    }

    @GetMapping("/my-companies")
    public ResponseEntity<?> myCompanies(@RequestParam String email) {
        return ResponseEntity.ok(companyAccessService.getUserCompanies(email));
    }
}
