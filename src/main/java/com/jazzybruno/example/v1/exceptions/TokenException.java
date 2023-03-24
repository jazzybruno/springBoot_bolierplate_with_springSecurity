package com.jazzybruno.example.v1.exceptions;

import com.jazzybruno.example.v1.dto.responses.ErrorResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class TokenException extends Exception{
  private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

  TokenException(String message){
      super(message);
  }

  ResponseEntity<Response> getResponseEntity(){
      List<String> details = new ArrayList<>();
      details.add(super.getMessage());
      ErrorResponse errorResponse = new ErrorResponse().setMessage("You do not have authority to access this resources").setDetails(details);
      Response<ErrorResponse> response = new Response<>();
  }
}
