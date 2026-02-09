package club.p6e.coat.resource.error;

import club.p6e.coat.common.exception.CustomException;
import club.p6e.coat.common.exception.DownloadNodeException;

/**
 * @author lidashuang
 * @version 1.0
 */
public class NodeException extends CustomException {
    public static final int DEFAULT_CODE = 11000;
    private static final String DEFAULT_SKETCH = "DOWNLOAD_NODE_EXCEPTION";

    public NodeException(Class<?> sc, String error, String content) {
        super(sc, DownloadNodeException.class, error, 11000, "DOWNLOAD_NODE_EXCEPTION", content);
    }

    public NodeException(Class<?> sc, Throwable throwable, String content) {
        super(sc, DownloadNodeException.class, throwable, 11000, "DOWNLOAD_NODE_EXCEPTION", content);
    }

    public NodeException(Class<?> sc, String error, int code, String sketch, String content) {
        super(sc, DownloadNodeException.class, error, code, sketch, content);
    }

    public NodeException(Class<?> sc, Throwable throwable, int code, String sketch, String content) {
        super(sc, DownloadNodeException.class, throwable, code, sketch, content);
    }
}
