package club.p6e.coat.resource;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface FileWriterBuilder {

    FileWriter execute(String type, Map<String, Object> attributes, MultipartFile source, String target);

}
