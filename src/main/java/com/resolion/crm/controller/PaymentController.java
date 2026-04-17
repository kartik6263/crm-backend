package com.resolion.crm.controller;

import com.resolion.crm.services.PaymentService;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

   /* @PostMapping("/create-order")
    public String createOrder() throws Exception {
        return paymentService.createOrder();
    }*/
   @PostMapping("/create-order")
   public ResponseEntity<?> createOrder() throws Exception {

       RazorpayClient client = new RazorpayClient("KEY_ID", "KEY_SECRET");

       JSONObject options = new JSONObject();
       options.put("amount", 50000); // ₹500
       options.put("currency", "INR");
       options.put("receipt", "txn_123");

       Order order = client.orders.create(options);
      // return ResponseEntity.ok(order.toString());
       return ResponseEntity.ok(paymentService.createOrder());
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


    /*@PostMapping("/create-order")
    public ResponseEntity<?> createOrder() throws Exception {
        return ResponseEntity.ok(paymentService.createOrder());
    }

    @PostMapping("/success")
    public String paymentSuccess(@RequestParam Long companyId,
                                 @RequestParam String paymentId) {
        return paymentService.activateSubscription(companyId, paymentId);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody Map<String, String> data) {
        return "Payment Verified";
    }*/


}