package club.p6e.coat.message.center;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherStartingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("")
public class Controller {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /**
     * Message Center Service Object
     */
    private final MessageCenterService service;

    /**
     * Message Center Properties Object
     */
    private final MessageCenterProperties properties;

    /**
     * Construct Initialization
     *
     * @param service    Message Center Service Object
     * @param properties Message Center Properties Object
     */
    @SuppressWarnings("ALL")
    public Controller(MessageCenterService service, MessageCenterProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @PostMapping("/push")
    public ResultContext push(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "recipients", required = false) String recipients,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) {
        if (id == null || data == null || language == null || recipients == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(...) >>> request parameter [id/data/language/recipients] exception",
                    "request parameter [id/data/language/recipients] exception"
            );
        }
        LOGGER.info("REQUEST PARAM ID >>> {}", id);
        LOGGER.info("REQUEST PARAM DATA >>> {}", data);
        LOGGER.info("REQUEST PARAM LANGUAGE >>> {}", language);
        LOGGER.info("REQUEST PARAM RECIPIENTS >>> {}", recipients);
        LOGGER.info("REQUEST PARAM FILES >>> {}", files);
        final List<String> pRecipients;
        final Map<String, String> pData;
        final List<File> pFiles = new ArrayList<>();
        try {
            final List<String> l = JsonUtil.fromJsonToList(recipients, String.class);
            final Map<String, Object> m = JsonUtil.fromJsonToMap(data, String.class, Object.class);
            pData = new HashMap<>();
            pRecipients = new ArrayList<>();
            if (l != null) {
                pRecipients.addAll(l);
            }
            if (m != null) {
                for (final String key : m.keySet()) {
                    pData.put(key, String.valueOf(m.get(key)));
                }
            }
            if (files != null) {
                final String num = GeneratorUtil.uuid();
                final AtomicInteger aai = new AtomicInteger(0);
                for (final MultipartFile file : files) {
                    final String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
                    String name = FileUtil.getName(filename);
                    name = name == null ? "" : name;
                    String suffix = FileUtil.getSuffix(filename);
                    suffix = suffix == null ? "" : suffix;
                    final String an = name.toUpperCase().startsWith("EMBEDDED-") ?
                            name : ("ATTACHMENT" + (aai.getAndIncrement() == 0 ? "" : ("-" + (aai.get() - 1))));
                    final File out = new File(FileUtil.composePath(properties.getTmpResourcePath(), FileUtil.composePath(num, FileUtil.composeFile(an, suffix))));
                    FileUtil.createFolder(out.getParentFile());
                    file.transferTo(out);
                    pFiles.add(out);
                }
            }
        } catch (Exception e) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(...) >>> " + e.getMessage(),
                    "request parameter exception"
            );
        }
        LOGGER.info("PUSH >>> [START]");
        LOGGER.info("ID ::: {}", id);
        LOGGER.info("DATA ::: {}", pData);
        LOGGER.info("FILE ::: {}", pFiles);
        LOGGER.info("LANGUAGE ::: {}", language);
        LOGGER.info("RECIPIENTS ::: {}", pRecipients);
        final LauncherResultModel result = service.execute(new LauncherStartingModel() {

            @Override
            public Integer id() {
                return id;
            }

            @Override
            public String language() {
                return language;
            }

            @Override
            public Map<String, String> param() {
                return pData;
            }

            @Override
            public List<File> attachment() {
                return pFiles;
            }

            @Override
            public List<String> recipients() {
                return pRecipients;
            }

        });
        LOGGER.info("PUSH >>>   [END]");
        return ResultContext.build(result);
    }

}
