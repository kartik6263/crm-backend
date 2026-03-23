package com.leadmatrix.crm.services;

import com.leadmatrix.crm.entity.LeadmatrixEntity;
import org.springframework.stereotype.Service;

@Service
public class LeadScoringService {

    public int calculateScore(LeadmatrixEntity lead){

        int score = 0;

        if("Facebook".equals(lead.getSource()))
            score += 30;

        if("Website".equals(lead.getSource()))
            score += 40;

        if("Interested".equals(lead.getStatus()))
            score += 50;

        if("CALL".equals(lead.getLastActivity()))
            score += 20;

        return score;

    }

}
