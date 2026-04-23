package com.resolion.crm.respository;

import com.resolion.crm.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    List<Notebook> findByCompanyIdAndUserEmailOrderByIdDesc(Long companyId, String userEmail);
}