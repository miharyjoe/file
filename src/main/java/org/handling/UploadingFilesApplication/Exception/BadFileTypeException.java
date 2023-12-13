package org.handling.UploadingFilesApplication.Exception;

public class BadFileTypeException extends RuntimeException {
  public BadFileTypeException(String message) {
    super(message);
  }
}
