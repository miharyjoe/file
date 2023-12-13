package org.handling.UploadingFilesApplication.Exception;

public class SensitiveFileException extends RuntimeException {
  public SensitiveFileException(String message) {
    super(message);
  }
}

