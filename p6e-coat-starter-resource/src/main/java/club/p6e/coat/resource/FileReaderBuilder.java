package club.p6e.coat.resource;

import org.springframework.http.MediaType;

import java.io.File;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface FileReaderBuilder {

    FileReader build();

    FileReaderBuilder fileName(String fileName);

    FileReaderBuilder fileMediaType(MediaType fileMediaType);


    FileReaderBuilder attributes(Map<String, Object> attributes);

    FileReaderBuilder fileSuffix(String fileSuffix);

    FileReaderBuilder filePath(String filePath);

    FileReaderBuilder of(File file);

    FileReaderBuilder of();
}
