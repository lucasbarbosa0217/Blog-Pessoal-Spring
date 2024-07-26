package com.generation.blogpessoal.service;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class ImageService {

    @Value("${firebase.secret}")
    private String connectionString;

  private String uploadFile(File file, String fileName) throws IOException {
      InputStream inputStream = new ByteArrayInputStream(connectionString.getBytes(StandardCharsets.UTF_8));
      fileName = "userProfileImage/"+ fileName;
      BlobId blobId = BlobId.of("blog-a5aab.appspot.com", fileName); // Replace with your bucker name
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
      Credentials credentials = GoogleCredentials.fromStream(inputStream);
      Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
      Blob upload = storage.create(blobInfo, Files.readAllBytes(file.toPath()));

      String fileNameEncoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

      return "https://firebasestorage.googleapis.com/v0/b/blog-a5aab.appspot.com/o/"+fileNameEncoded+"?alt=media";
  }

  private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
      File tempFile = new File(fileName);
      try (FileOutputStream fos = new FileOutputStream(tempFile)) {
          fos.write(multipartFile.getBytes());
          fos.close();
      }
      return tempFile;
  }

  private String getExtension(String fileName) {
      return fileName.substring(fileName.lastIndexOf("."));
  }


  public String upload(MultipartFile multipartFile) {
      try {
          String fileName = multipartFile.getOriginalFilename();                      
          fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));
          File file = this.convertToFile(multipartFile, fileName);                
          String URL = this.uploadFile(file, fileName);                                
          file.delete();
          return URL;
      } catch (Exception e) {
          e.printStackTrace();
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao fazer upload da imagem");
      }
  }

}