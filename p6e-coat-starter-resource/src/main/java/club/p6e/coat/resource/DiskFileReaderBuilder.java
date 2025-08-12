package club.p6e.coat.resource;

import java.io.File;

/**
 * @author lidashuang
 * @version 1.0
 */
public class DiskFileReaderBuilder {

    public static DiskFileReaderBuilder of(File file) {
        return new DiskFileReaderBuilder();
    }

    public static DiskFileReaderBuilder of(File[] files) {
        return new DiskFileReaderBuilder();
    }

    public FileReader build() {

    }

    public DiskFileReaderBuilder fileName(String fileName) {
    }

    public DiskFileReaderBuilder fileSuffix(String fileName) {

    }
}
