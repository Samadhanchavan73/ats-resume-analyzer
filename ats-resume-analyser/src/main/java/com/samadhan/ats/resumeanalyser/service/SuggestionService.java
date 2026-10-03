package com.samadhan.ats.resumeanalyser.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class SuggestionService {

    public String generateSuggestions(String resumeText, String jdText, String companyType) {

        String url = "http://localhost:11434/api/generate";

        RestTemplate restTemplate = new RestTemplate();

        // ✅ Short prompt (tinyllama friendly)
      /*  String prompt = "You are a professional ATS (Applicant Tracking System).\n"
                + "Analyze the resume based on the job description.\n\n"

                + "Return ONLY in this format:\n\n"

                + "ATS Score: <0-100>\n"
                + "Technical Skills: <0-100>\n"
                + "Communication: <0-100>\n"
                + "Experience: <0-100>\n\n"

                + "Missing Keywords:\n"
                + "- List ONLY important keywords from Job Description that are NOT present in Resume\n"
                + "- Minimum 5 keywords required\n\n"

                + "Suggestions:\n"
                + "- Give 5 improvement suggestions\n\n"

                + "IMPORTANT RULES:\n"
                + "- Never say 'None'\n"
                + "- Always give at least 5 missing keywords\n"
                + "- Always give suggestions\n"
                + "- Do NOT repeat resume\n"
                + "- Keep output structured\n\n"

                + "Resume:\n" + resumeText + "\n\n"
                + "Job Description:\n" + jdText;
*/
        String prompt = "Give output EXACTLY in this format:\n"
                + "ATS Score: <number>\n"
                + "Technical Skills: <number>\n"
                + "Communication: <number>\n"
                + "Experience: <number>\n\n"
                + "Return scores ONLY between 0 to 100 (no decimals).\n\n"
                + "Also give Missing Keywords and Suggestions.\n\n"
                + "Resume:\n" + resumeText;

        // ✅ Body
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3");
       // body.put("model", "tinyllama:latest");
        body.put("prompt", prompt);
        body.put("stream", false);

        // ✅ Headers (IMPORTANT FIX)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // ✅ POST request (correct)
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().get("response") != null) {
                return response.getBody().get("response").toString();
            } else {
                return "No AI response received";
            }

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 for debugging
            return "Error fetching AI response: " + e.getMessage();
        }
    }
}