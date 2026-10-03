package com.jalamdas.ats.resumeanalyser.service;

import com.jalamdas.ats.resumeanalyser.model.AnalysisReport;
import com.jalamdas.ats.resumeanalyser.model.User;
import com.jalamdas.ats.resumeanalyser.repository.AnalysisReportRepository;
import com.jalamdas.ats.resumeanalyser.repository.UserRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnalysisService {

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalysisReportRepository reportRepository;

    public record AnalysisResult(
            String summary,
            String aiSuggestions,
            List<String> complianceWarnings
    ) {}

    public AnalysisResult analyzeAndSave(String resumeText, String jdText, String companyType, MultipartFile file, String username) {

        AnalysisResult result = analyze(resumeText, jdText, companyType, file);

        // ✅ FIXED user fetch
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        AnalysisReport report = new AnalysisReport();
        report.setUser(user);
        report.setFileName(file.getOriginalFilename());
        report.setJobDescriptionSummary(jdText.substring(0, Math.min(jdText.length(), 100)) + "...");
        report.setAiReport(result.aiSuggestions());
        report.setComplianceWarnings(String.join("\n", result.complianceWarnings()));
        report.setAnalysisDate(LocalDateTime.now());

        reportRepository.save(report);

        return result;
    }

    private AnalysisResult analyze(String resumeText, String jdText, String companyType, MultipartFile file) {

        List<String> complianceWarnings = runComplianceChecks(resumeText, file);

        String aiFeedback = suggestionService.generateSuggestions(resumeText, jdText, companyType);

        // ✅ FINAL parsing
        List<String> missingKeywords = extractMissingKeywords(resumeText, jdText);

        String summary = parseScores(aiFeedback, resumeText, jdText)
                + "\n\nMissing Keywords:\n- "
                + String.join("\n- ", missingKeywords);


        return new AnalysisResult(summary, aiFeedback, complianceWarnings);
    }

    // 🔥 FINAL parsing (smart + fallback)
    private String parseScores(String text, String resumeText, String jdText) {

        int atsScore = extractScore(text, "ATS Score");
        int techScore = extractScore(text, "Technical Skills");
        int commScore = extractScore(text, "Communication");
        int expScore = extractScore(text, "Experience");

        // 🔥 fallback logic
        if (atsScore == -1) atsScore = calculateKeywordMatch(resumeText, jdText);
        if (techScore == -1) techScore = 70;
        if (commScore == -1) commScore = 65;
        if (expScore == -1) expScore = 60;

        return "📊 Resume Scorecard\n"
                + "ATS Score: " + atsScore + "\n"
                + "Technical Skills: " + techScore + "\n"
                + "Communication: " + commScore + "\n"
                + "Experience: " + expScore;
    }

    // 🔥 REGEX BASED SCORE EXTRACTION (FIXED)
    /*private int extractScore(String text, String key) {
        try {
            Pattern pattern = Pattern.compile(key + "\\s*:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                int score = Integer.parseInt(matcher.group(1));
                return Math.min(score, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
*/
    private int extractScore(String text, String key) {
        try {
            Pattern pattern = Pattern.compile(key + "\\s*:?\\s*(\\d+(\\.\\d+)?)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                double score = Double.parseDouble(matcher.group(1));

                // 🔥 convert to 0–100 scale if needed
                if (score <= 10) {
                    score = score * 10;
                }

                return Math.min((int) score, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<String> extractMissingKeywords(String resume, String jd) {

        String[] jdWords = jd.toLowerCase().split("\\W+");
        String resumeText = resume.toLowerCase();

        Set<String> missing = new HashSet<>();

        for (String word : jdWords) {
            if (word.length() > 4 && !resumeText.contains(word)) {
                missing.add(word);
            }
        }

        return new ArrayList<>(missing).subList(0, Math.min(5, missing.size()));
    }

    // 🔥 FALLBACK LOGIC (VERY IMPORTANT)
    private int calculateKeywordMatch(String resume, String jd) {

        String[] jdWords = jd.toLowerCase().split("\\W+");
        String resumeText = resume.toLowerCase();

        int match = 0;

        for (String word : jdWords) {
            if (resumeText.contains(word)) {
                match++;
            }
        }

        if (jdWords.length == 0) return 50;

        int score = (match * 100) / jdWords.length;
        return Math.min(score, 100);
    }

    private List<String> runComplianceChecks(String resumeText, MultipartFile file) {
        List<String> warnings = new ArrayList<>();
        String filename = file.getOriginalFilename();

        if (filename != null && !filename.endsWith(".pdf") && !filename.endsWith(".docx")) {
            warnings.add("File Type: You uploaded a '" + filename.substring(filename.lastIndexOf(".") + 1) + "' file. Use .pdf or .docx.");
        }

        Pattern pattern = Pattern.compile("\\b(0[1-9]|1[0-2])/(\\d{4})\\b|\\b(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s(\\d{4})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(resumeText);
        Set<String> dateFormatsFound = new HashSet<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) dateFormatsFound.add("MM/YYYY");
            if (matcher.group(3) != null) dateFormatsFound.add("Month YYYY");
        }

        if (dateFormatsFound.size() > 1) {
            warnings.add("Date Format: Inconsistent formats found.");
        }

        if (filename != null && filename.endsWith(".docx")) {
            warnings.addAll(checkDocxCompliance(file));
        }

        return warnings;
    }

    private List<String> checkDocxCompliance(MultipartFile file) {
        List<String> warnings = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {

            if (document.getAllPictures() != null && !document.getAllPictures().isEmpty()) {
                warnings.add("Images found in resume.");
            }

            List<XWPFTable> tables = document.getTables();
            if (tables != null && !tables.isEmpty()) {
                warnings.add("Tables found in resume.");
            }

        } catch (Exception e) {
            warnings.add("Error reading .docx file.");
        }

        return warnings;
    }
}