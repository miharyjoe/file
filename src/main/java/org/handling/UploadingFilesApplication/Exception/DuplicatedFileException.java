package org.handling.UploadingFilesApplication.Exception;

public class DuplicatedFileException extends RuntimeException {
  public DuplicatedFileException(String message) {
    super(message);
  }
}

