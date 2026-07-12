package in.springproject.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * AI Service integrating with OpenRouter API.
 * Provides AI-powered features:
 * - Academic performance analysis
 * - Attendance risk prediction
 * - Personalized study recommendations
 * - Progress report generation
 * - Chatbot assistant
 */
@Service
@Slf4j
public class AiService {

    private final WebClient webClient;

    @Value("${app.ai.openrouter.api-key:}")
    private String apiKey;

    @Value("${app.ai.openrouter.model:mistralai/mistral-7b-instruct:free}")
    private String model;

    @Value("${app.ai.openrouter.max-tokens:2048}")
    private int maxTokens;

    public AiService(@Value("${app.ai.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    /** Chat with default system prompt (academic assistant context). */
    public String chat(String userMessage) {
        return chat(userMessage,
            "You are an expert academic assistant for a Student Management System. " +
            "Help students with study advice, performance analysis, and academic guidance.");
    }

    /** Chat with a custom system prompt. */
    public String chat(String userMessage, String systemPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI service is not configured. Please set the app.ai.openrouter.api-key property.";
        }
        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
                )
            );

            Map<?, ?> response = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header("HTTP-Referer", "https://sms.edu")
                .header("X-Title", "Student Management System")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) choice.get("message");
                    if (message != null) return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage());
        }
        return "I'm sorry, I couldn't process your request at this time. Please try again later.";
    }

    /** Analyze student academic performance and generate insights. */
    public String analyzeStudentPerformance(String studentName, double cgpa,
            double attendancePercentage, String recentGrades) {
        String prompt = String.format(
            "Analyze this student's academic performance:\n\n" +
            "Student: %s\nCGPA: %.2f / 4.0\nAttendance: %.1f%%\nRecent Grades: %s\n\n" +
            "Provide:\n1. Performance assessment (Excellent/Good/Average/Below Average)\n" +
            "2. Key strengths\n3. Areas for improvement\n4. Specific study recommendations\n" +
            "5. Risk factors (if any)\n6. Motivational message",
            studentName, cgpa, attendancePercentage, recentGrades);
        return chat(prompt, "You are an academic performance analyst providing constructive feedback.");
    }

    /** Predict attendance risk and provide a recovery plan. */
    public String predictAttendanceRisk(String studentName, double currentAttendance,
            int remainingClasses, int totalClasses) {
        String prompt = String.format(
            "Attendance Risk Analysis:\n\nStudent: %s\nCurrent attendance: %.1f%%\n" +
            "Remaining classes: %d\nTotal classes: %d\nRequired: 75%%\n\n" +
            "Provide:\n1. Risk level (HIGH/MEDIUM/LOW)\n2. Classes needed to reach 75%%\n" +
            "3. Can the student recover? How?\n4. Consequences of current trend\n5. Action plan",
            studentName, currentAttendance, remainingClasses, totalClasses);
        return chat(prompt);
    }

    /** Generate personalized study recommendations. */
    public String generateStudyRecommendations(String studentName, String courses,
            String weakSubjects, String learningStyle) {
        String prompt = String.format(
            "Generate personalized study recommendations:\n\nStudent: %s\nCourses: %s\n" +
            "Weak Subjects: %s\nLearning Style: %s\n\n" +
            "Provide:\n1. Weekly study schedule\n2. Resources for weak subjects\n" +
            "3. Study techniques for their learning style\n4. Time management tips\n5. Exam prep strategy",
            studentName, courses, weakSubjects, learningStyle);
        return chat(prompt);
    }

    /** Generate a formal progress report summary. */
    public String generateProgressReport(String studentName, String semester,
            double cgpa, double attendance, String courseSummary) {
        String prompt = String.format(
            "Generate a formal student progress report:\n\n" +
            "Student: %s | Semester: %s\nCGPA: %.2f | Attendance: %.1f%%\nCourse Performance: %s\n\n" +
            "Write a report including:\n- Executive summary\n- Academic performance analysis\n" +
            "- Attendance analysis\n- Course-wise performance\n- Overall assessment\n- Next semester recommendations",
            studentName, semester, cgpa, attendance, courseSummary);
        return chat(prompt, "You are an academic counselor writing formal student progress reports.");
    }
}
