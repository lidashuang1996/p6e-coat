package club.p6e.coat.resource;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.resource.error.ResourcePathException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

/**
 * Simple File Attribute Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FileAttributeBuilder.class)
public class SimpleFileAttributeBuilder implements FileAttributeBuilder {

    @Override
    public FileAttribute build(File file, Map<String, Object> resourceAttributes) {
        if (file == null) {
            throw new ResourcePathException(
                    this.getClass(),
                    "fun build(File file, Map<String, Object> resourceAttributes)",
                    "file resource path exception"
            );
        }
        String fileRule = TransformationUtil.objectToString(resourceAttributes.get("fileRule"));
        final String fileMediaType = TransformationUtil.objectToString(resourceAttributes.get("fileMediaType"));
        if (file.isFile()) {
            return new SimpleFileAttribute(file.getName(), file.length(),
                    fileMediaType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.valueOf(fileMediaType));
        } else {
            long len = 0;
            String name = FileUtil.getName(file.getName());
            final String fileSuffix = FileUtil.getSuffix(file.getName());
            final String fileContentPath = FileUtil.composeFile(fileRule == null ? "source" : fileRule, fileSuffix);
            final File txt = new File(file, "name.txt");
            final File content = new File(file, fileContentPath);
            if (FileUtil.checkFileExist(txt)) {
                try {
                    name = Files.readString(txt.toPath()).trim();
                } catch (Exception e) {
                    // ignore exception
                }
            }
            if (FileUtil.checkFileExist(content)) {
                len = content.length();
            }
            return new SimpleFileAttribute(name, len, fileMediaType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.valueOf(fileMediaType));
        }
    }

}
