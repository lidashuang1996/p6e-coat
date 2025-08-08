package club.p6e.coat.resource;

import com.sun.tools.sjavac.Util;

import java.io.File;

/**
 * @author lidashuang
 * @version 1.0
 */
public class FileReaderBuilder {

    public static FileReaderBuilder of(File file) {
        return new FileReaderBuilder();
    }

    public static FileReaderBuilder of(File[] files) {
        return new FileReaderBuilder();
    }

    public FileReader build() {

    }

    public FileReaderBuilder fileName(String fileName) {
    }

    public FileReaderBuilder fileSuffix(String fileName) {
    }
}
