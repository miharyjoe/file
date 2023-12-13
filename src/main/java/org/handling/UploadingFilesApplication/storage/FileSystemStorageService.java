package org.handling.UploadingFilesApplication.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.handling.UploadingFilesApplication.Exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

  private final Path rootLocation;

  private final long maxFileSize = 5000 * 1024;

  @Autowired
  public FileSystemStorageService(StorageProperties properties) {

    if(properties.getLocation().trim().length() == 0){
      throw new StorageException("File upload location can not be Empty.");
    }

    this.rootLocation = Paths.get(properties.getLocation());
  }

  @Override
  public void store(MultipartFile file) {
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file.");
      }
      if(file.getSize() > maxFileSize){
        throw new FileTooLargeException("File size exceeds the allowed limit");
      }
      if (isFileTypeAllowed(file.getOriginalFilename())) {
        throw new BadFileTypeException("Invalid or disallowed file type.");
      }
      if (!isFilenameValid(file.getOriginalFilename())) {
        throw new FilenameInvalidException("Invalid filename.");
      }
      Path destinationFile = this.rootLocation.resolve(
          Paths.get(file.getOriginalFilename()))
        .normalize().toAbsolutePath();

      if (Files.exists(destinationFile)) {
        throw new DuplicatedFileException("A file with the same name already exists.");
      }

      if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
        // This is a security check
        throw new StorageException(
          "Cannot store file outside current directory.");
      }

      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile,
          StandardCopyOption.REPLACE_EXISTING);
      }
    }
    catch (IOException e) {
      throw new StorageException("Failed to store file.", e);
    }
  }
  //File ends with .txt is not allowed
  private boolean isFileTypeAllowed(String filename) {
    return filename.endsWith(".txt");
  }

  private boolean isFilenameValid(String filename) {
    // filenames must not contain spaces
    return !filename.contains(" ");
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.rootLocation, 1)
        .filter(path -> !path.equals(this.rootLocation))
        .map(this.rootLocation::relativize);
    }
    catch (IOException e) {
      throw new StorageException("Failed to read stored files", e);
    }

  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      }
      else {
        throw new StorageFileNotFoundException(
          "Could not read file: " + filename);

      }
    }
    catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    }
    catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }
}
