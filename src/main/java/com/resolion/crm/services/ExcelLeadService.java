package com.resolion.crm.services;

import com.resolion.crm.entity.LeadActivity;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.respository.ActivityRepository;
import com.resolion.crm.respository.LeadmatrixRespository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Service
public class ExcelLeadService {

    @Autowired
    private LeadmatrixRespository leadmatrixRepository;

    @Autowired
    private ActivityRepository activityRepository;

   /* public void importLeads(MultipartFile file) throws Exception {
        Workbook workbook =
                new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for(Row row : sheet){
            if(row.getRowNum()==0) continue;
            LeadmatrixEntity lead = new LeadmatrixEntity();
            lead.setName(row.getCell(0).getStringCellValue());
            lead.setEmail(row.getCell(1).getStringCellValue());
            lead.setPhone(row.getCell(2).getStringCellValue());
            leadmatrixRepository.save(lead);
        }
    }*/
   public void importLeads(MultipartFile file) throws Exception {
       try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
           Sheet sheet = workbook.getSheetAt(0);

           for (Row row : sheet) {
               if (row.getRowNum() == 0) continue;
               if (row.getCell(0) == null) continue;

               LeadmatrixEntity lead = new LeadmatrixEntity();
               lead.setName(row.getCell(0).getStringCellValue());
               lead.setEmail(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "");
               lead.setPhone(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "");
               lead.setStatus("NEW");
               lead.setSource("Excel Import");
               lead.setCreatedDate(LocalDate.now().toString());

               LeadmatrixEntity saved = leadmatrixRepository.save(lead);

               LeadActivity activity = new LeadActivity();
               activity.setLeadId(saved.getId());
               activity.setActivityType("LEAD_IMPORTED");
               activity.setDescription("Lead imported from Excel");
               activity.setActivityDate(LocalDate.now().toString());
               activityRepository.save(activity);
           }
       }
   }
}
