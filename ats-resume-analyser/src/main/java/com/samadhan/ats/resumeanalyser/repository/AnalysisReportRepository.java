package com.samadhan.ats.resumeanalyser.repository;

import com.samadhan.ats.resumeanalyser.model.AnalysisReport;
import com.samadhan.ats.resumeanalyser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
    
    List<AnalysisReport> findByUserOrderByAnalysisDateDesc(User user);

}