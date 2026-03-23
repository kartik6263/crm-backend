package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.services.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public String createOrder() throws Exception {
        return paymentService.createOrder();
    }

    @PostMapping("/success")
    public String paymentSuccess(@RequestParam Long companyId,
                                 @RequestParam String paymentId){

        return paymentService.activateSubscription(companyId, paymentId);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody Map<String, String> data) {
        return "Payment Verified";
    }
}