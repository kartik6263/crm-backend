package com.resolion.crm.service;

import com.resolion.crm.entity.LeadActivity;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.repository.ActivityRepository;
import com.resolion.crm.repository.LeadmatrixRespository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import com.resolion.crm.enums.LeadSource;
import com.resolion.crm.enums.LeadStatus;

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

//               LeadmatrixEntity lead = new LeadmatrixEntity();
//               lead.setname(
//                       row.getCell(0).getStringCellValue()
//               );
              // lead.set(row.getCell(0).getStringCellValue());
               LeadmatrixEntity lead = new LeadmatrixEntity();

               String fullName =
                       row.getCell(0).getStringCellValue();

               String[] parts = fullName.split(" ", 2);

               lead.setFirstName(parts[0]);

               if (parts.length > 1) {
                   lead.setLastName(parts[1]);
               }

               lead.setEmail(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "");
               lead.setPhone(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "");
              // lead.setStatus()("NEW");
               lead.setStatus(LeadStatus.NEW);
              // lead.setSource("source");
               lead.setSource(LeadSource.EXCEL_IMPORT);
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
