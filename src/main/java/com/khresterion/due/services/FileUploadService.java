/**
 * 
 */
package com.khresterion.due.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author khresterion
 *
 */
@Service
public interface FileUploadService {

  int save(MultipartFile file) throws IOException;

}
