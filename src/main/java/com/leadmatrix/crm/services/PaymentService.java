package com.leadmatrix.crm.services;

import com.leadmatrix.crm.respository.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.leadmatrix.crm.entity.Subscription;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class PaymentService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    /*public String createOrder() throws Exception {

        RazorpayClient client = new RazorpayClient("KEY","SECRET");

        JSONObject options = new JSONObject();
        options.put("amount", 50000);
        options.put("currency", "INR");

        Order order = client.orders.create(options);

        return order.toString();
    }

    public String activateSubscription(Long companyId, String paymentId) {

        // 1️⃣ Fetch existing subscription (if exists)
        Subscription sub = subscriptionRepository.findByCompanyId(companyId)
                .orElse(new Subscription());

        // 2️⃣ Set / Update values
        sub.setCompanyId(companyId);
        sub.setPlan("PRO");
        sub.setStatus("ACTIVE");
        sub.setPaymentId(paymentId);

        // 3️⃣ Save (update or insert)
        subscriptionRepository.save(sub);

        return "Subscription Activated Successfully";
    }*/


    public String createOrder() throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject options = new JSONObject();
        options.put("amount", 50000);
        options.put("currency", "INR");
        options.put("receipt", "txn_123");

        Order order = client.orders.create(options);
        return order.toString();
    }

    public String activateSubscription(Long companyId, String paymentId) {
        Subscription sub = subscriptionRepository.findByCompanyId(companyId)
                .orElse(new Subscription());

        sub.setCompanyId(companyId);
        sub.setPlan("PRO");
        sub.setStatus("ACTIVE");
        sub.setPaymentId(paymentId);

        subscriptionRepository.save(sub);
        return "Subscription Activated Successfully";
    }

}