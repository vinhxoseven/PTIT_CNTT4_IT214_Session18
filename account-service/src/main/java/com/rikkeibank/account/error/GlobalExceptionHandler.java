package com.rikkeibank.account.error;
import jakarta.servlet.http.HttpServletRequest; import java.time.Instant; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice public class GlobalExceptionHandler {
  @ExceptionHandler(ResponseStatusException.class) ResponseEntity<ApiError> status(ResponseStatusException ex,HttpServletRequest req){int s=ex.getStatusCode().value();return ResponseEntity.status(s).body(new ApiError(Instant.now(),s,ex.getStatusCode().toString(),ex.getReason(),req.getRequestURI()));}
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex,HttpServletRequest req){String m=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Dữ liệu không hợp lệ");return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"BAD_REQUEST",m,req.getRequestURI()));}
  @ExceptionHandler(Exception.class) ResponseEntity<ApiError> general(Exception ex,HttpServletRequest req){return ResponseEntity.status(500).body(new ApiError(Instant.now(),500,"INTERNAL_SERVER_ERROR",ex.getMessage(),req.getRequestURI()));}
}
