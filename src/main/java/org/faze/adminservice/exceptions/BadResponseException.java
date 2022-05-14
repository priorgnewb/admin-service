package org.faze.adminservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class BadResponseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BadResponseException(String message) {
    super(message);
  }

  public BadResponseException(String message, Throwable cause) {
    super(message, cause);
  }
}