package com.resolion.crm.Scheduler;

import com.resolion.crm.entity.CaseEntity;
import com.resolion.crm.ENUMS.CaseStatus;
import com.resolion.crm.respository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseEscalationScheduler {

    private final CaseRepository repository;

    @Scheduled(fixedRate = 300000)
    public void checkEscalations(){

        List<CaseEntity> openCases =
                repository.findAll();

        for(CaseEntity c : openCases){

            if(c.getResolutionDue() != null &&
                    LocalDateTime.now().isAfter(c.getResolutionDue())){

                c.setStatus(CaseStatus.ESCALATED);

                c.setBreached(true);

                c.setBreachTime(LocalDateTime.now());

                repository.save(c);

                log.info("Case Escalated: {}", c.getCaseNumber());
            }
        }
    }
}