package in.springproject.ai;

import in.springproject.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for AI-powered academic features.
 * Endpoint: /api/v1/ai
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Features", description = "AI-powered academic analysis and chatbot APIs")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI academic assistant")
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Message is required", 400));
        }
        return ResponseEntity.ok(ApiResponse.success("AI response", aiService.chat(message)));
    }

    @PostMapping("/analyze-performance")
    @Operation(summary = "AI-powered student performance analysis")
    public ResponseEntity<ApiResponse<String>> analyzePerformance(@RequestBody Map<String, String> request) {
        String analysis = aiService.analyzeStudentPerformance(
            request.getOrDefault("studentName", "Student"),
            Double.parseDouble(request.getOrDefault("cgpa", "0")),
            Double.parseDouble(request.getOrDefault("attendancePercentage", "0")),
            request.getOrDefault("recentGrades", "N/A"));
        return ResponseEntity.ok(ApiResponse.success("Performance analysis", analysis));
    }

    @PostMapping("/attendance-risk")
    @Operation(summary = "Predict attendance risk and suggest recovery plan")
    public ResponseEntity<ApiResponse<String>> attendanceRisk(@RequestBody Map<String, String> request) {
        String risk = aiService.predictAttendanceRisk(
            request.getOrDefault("studentName", "Student"),
            Double.parseDouble(request.getOrDefault("currentAttendance", "0")),
            Integer.parseInt(request.getOrDefault("remainingClasses", "0")),
            Integer.parseInt(request.getOrDefault("totalClasses", "0")));
        return ResponseEntity.ok(ApiResponse.success("Attendance risk analysis", risk));
    }

    @PostMapping("/study-recommendations")
    @Operation(summary = "Generate personalized study recommendations")
    public ResponseEntity<ApiResponse<String>> studyRecommendations(@RequestBody Map<String, String> request) {
        String analysis = aiService.generateStudyRecommendations(
            request.getOrDefault("studentName", "Student"),
            request.getOrDefault("courses", "N/A"),
            request.getOrDefault("weakSubjects", "None"),
            request.getOrDefault("learningStyle", "Mixed"));
        return ResponseEntity.ok(ApiResponse.success("Study recommendations", analysis));
    }

    @PostMapping("/progress-report")
    @Operation(summary = "Generate AI-powered student progress report")
    public ResponseEntity<ApiResponse<String>> progressReport(@RequestBody Map<String, String> request) {
        String report = aiService.generateProgressReport(
            request.getOrDefault("studentName", "Student"),
            request.getOrDefault("semester", "Current"),
            Double.parseDouble(request.getOrDefault("cgpa", "0")),
            Double.parseDouble(request.getOrDefault("attendance", "0")),
            request.getOrDefault("courseSummary", "N/A"));
        return ResponseEntity.ok(ApiResponse.success("Progress report generated", report));
    }
}
