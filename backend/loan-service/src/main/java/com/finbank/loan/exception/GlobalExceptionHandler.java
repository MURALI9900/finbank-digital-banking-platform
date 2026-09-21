package com.finbank.loan.exception;
import jakarta.servlet.http.HttpServletRequest;import org.springframework.http.HttpStatus;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import java.time.LocalDateTime;import java.util.Map;
@RestControllerAdvice public class GlobalExceptionHandler{
 @ExceptionHandler(LoanException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,Object> loan(LoanException e,HttpServletRequest r){return error(400,e.getMessage(),r.getRequestURI());}
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,Object> validation(HttpServletRequest r){return error(400,"Validation failed",r.getRequestURI());}
 @ExceptionHandler(Exception.class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public Map<String,Object> generic(HttpServletRequest r){return error(500,"Unexpected error",r.getRequestURI());}
 private Map<String,Object> error(int s,String m,String p){return Map.of("timestamp",LocalDateTime.now(),"status",s,"message",m,"path",p);}
}