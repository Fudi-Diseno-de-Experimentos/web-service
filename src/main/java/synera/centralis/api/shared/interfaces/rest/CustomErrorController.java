package synera.centralis.api.shared.interfaces.rest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Error Controller to handle application errors
 */
//@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        
        // Get error attributes from request
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        
        errorDetails.put("timestamp", System.currentTimeMillis());
        errorDetails.put("status", statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", HttpStatus.valueOf(statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value()).getReasonPhrase());
        errorDetails.put("message", errorMessage != null ? errorMessage : "An unexpected error occurred");
        errorDetails.put("path", requestUri != null ? requestUri : "Unknown");

        HttpStatus status = statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(errorDetails);
    }
}
