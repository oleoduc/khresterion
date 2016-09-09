/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author khresterion
 *
 */
@Component
@Primary
public class GenericFileUploader implements FileUploadService {

    private static final String TMP_FOLDER = System.getProperty("java.io.tmpdir") + "/";
    
    /* (non-Javadoc)
     * @see com.khresterion.due.services.FileUploadService#save(org.springframework.web.multipart.MultipartFile)
     */
    @Override
    public int save(MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return 501;
          } else{
          byte[] bytes = file.getBytes();
          BufferedOutputStream stream =
                  new BufferedOutputStream(new FileOutputStream(new File(TMP_FOLDER + file.getOriginalFilename())));
          stream.write(bytes);
          stream.close();
          
          return 200;
        }
    }

}
