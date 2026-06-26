package club.p6e.coat.resource;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.JsonUtil;
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
@ConditionalOnMissingBean(SimpleFileAttributeBuilder.class)
public class SimpleFileAttributeBuilder implements FileAttributeBuilder {

    @Override
    public FileAttribute build(File file, Map<String, Object> fileResourceAttributes) {
        if (file == null) {
            throw new ResourcePathException(
                    this.getClass(),
                    "fun build(File file, Map<String, Object> fileResourceAttributes)",
                    "file resource path exception"
            );
        }
        String fileRule = TransformationUtil.objectToString(fileResourceAttributes.get("__rule__"));
        final String fileMediaType = TransformationUtil.objectToString(fileResourceAttributes.get("__media_type__"));
        if (file.isFile()) {
            return new SimpleFileAttribute(file.getName(), file.length(),
                    fileMediaType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.valueOf(fileMediaType));
        } else {
            long fileLength = 0;
            String fileName = FileUtil.getName(file.getName());
            final String fileSuffix = FileUtil.getSuffix(file.getName());
            final String fileContentPath = FileUtil.composeFile(fileRule == null ? "original" : "thumb_" + fileRule, fileSuffix);
            final File meta = new File(file, "meta.json");
            final File content = new File(file, fileContentPath);
            if (FileUtil.checkFileExist(content)) {
                fileLength = content.length();
            }
            if (FileUtil.checkFileExist(meta)) {
                try {
                    final String mc = Files.readString(meta.toPath()).trim();
                    if (!mc.isEmpty()) {
                        final Map<String, Object> mm = JsonUtil.fromJsonToMap(mc, String.class, Object.class);
                        final String name = TransformationUtil.objectToString(mm.get("name"));
                        if (name != null && !name.isEmpty()) {
                            fileName = name;
                        }
                    }
                } catch (Exception e) {
                    // ignore exception
                }
            }
            return new SimpleFileAttribute(fileName, fileLength,
                    fileMediaType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.valueOf(fileMediaType));
        }
    }

}
