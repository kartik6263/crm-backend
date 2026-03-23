package com.leadmatrix.crm.services;

import com.leadmatrix.crm.entity.LeadmatrixEntity;
import com.leadmatrix.crm.respository.LeadmatrixRespository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelLeadService {

    @Autowired
    private LeadmatrixRespository leadmatrixRepository;

    public void importLeads(MultipartFile file) throws Exception {

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

    }
}
