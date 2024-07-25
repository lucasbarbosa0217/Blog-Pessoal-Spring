package com.generation.blogpessoal.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class ImageService {

  private String uploadFile(File file, String fileName) throws IOException {
      BlobId blobId = BlobId.of("blog-a5aab.appspot.com", fileName); // Replace with your bucker name
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image").build();
      InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("blog-a5aab-firebase-adminsdk-iq4o2-9be4500547.json"); // change the file name with your one
      Credentials credentials = GoogleCredentials.fromStream(inputStream);
      Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
      storage.create(blobInfo, Files.readAllBytes(file.toPath()));

      String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/blog-a5aab.appspot.com/o/"+fileName+"?alt=media";
      return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
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