package com.resolion.crm.controller;

import com.razorpay.Subscription;
import com.resolion.crm.entity.CompanySetting;
import com.resolion.crm.respository.CompanySettingRepository;
import com.resolion.crm.respository.SubscriptionRepository;
import com.resolion.crm.services.PaymentService;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.resolion.crm.services.PlanService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CompanySettingRepository companySettingRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;


    @Autowired
    private PlanService planService;
   /* @PostMapping("/create-order")
    public String createOrder() throws Exception {
        return paymentService.createOrder();
    }*/
   @PostMapping("/create-order")

   public Map<String, Object> createOrder(@RequestParam Long companyId,
                                          @RequestParam String plan) throws Exception {

       int amount = planService.getPlanAmount(plan);

       JSONObject options = new JSONObject();
       options.put("amount", amount);
       options.put("currency", "INR");
       options.put("receipt", "txn_" + System.currentTimeMillis());

       Order order = razorpayClient.orders.create(options);

       return Map.of(
               "orderId", order.get("id"),
               "amount", amount,
               "plan", plan
       );
   }
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
                                 @RequestParam String paymentId,
                                 @RequestParam String plan){

        CompanySetting setting = companySettingRepository.findByCompanyId(companyId)
                .orElseThrow();

        setting.setPlanName(plan.toUpperCase());

        if ("PRO".equalsIgnoreCase(plan)) {
            setting.setMaxUsers(20);
            setting.setMaxLeads(5000);
            setting.setMaxAiReports(100);
            setting.setMaxExports(200);
        }

        if ("ENTERPRISE".equalsIgnoreCase(plan)) {
            setting.setMaxUsers(100);
            setting.setMaxLeads(50000);
            setting.setMaxAiReports(1000);
            setting.setMaxExports(1000);
        }

        companySettingRepository.save(setting);

        Subscription sub = new Subscription();
        sub.setCompanyId(companyId);
        sub.setPlan(plan);
        sub.setStatus("ACTIVE");
        sub.setPaymentId(paymentId);

        String now = LocalDateTime.now().toString();
        sub.setStartDate(now);

        int days = planService.getDurationDays(plan);
        sub.setEndDate(LocalDateTime.now().plusDays(days).toString());

        subscriptionRepository.save(sub);

        return "Subscription activated";
        return paymentService.activateSubscription(companyId, paymentId);
    }

    @GetMapping("/history")
    public List<Subscription> paymentHistory(@RequestParam Long companyId) {
        return subscriptionRepository.findByCompanyId(companyId);
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