package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.config.ConfigParserService;
import club.p6e.coat.message.center.config.mail.MailMessageConfigModel;
import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
import club.p6e.coat.message.center.config.sms.ShortMessageConfigModel;
import club.p6e.coat.message.center.config.telegram.TelegramMessageConfigModel;
import club.p6e.coat.message.center.config.wechat.WeChatMessageConfigModel;
import club.p6e.coat.message.center.error.*;
import club.p6e.coat.message.center.launcher.*;
import club.p6e.coat.message.center.launcher.mail.MailMessageLauncherService;
import club.p6e.coat.message.center.launcher.mobile.MobileMessageLauncherService;
import club.p6e.coat.message.center.launcher.sms.ShortMessageLauncherService;
import club.p6e.coat.message.center.launcher.telegram.TelegramMessageLauncherService;
import club.p6e.coat.message.center.launcher.wechat.WeChatMessageLauncherService;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.template.TemplateModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message Center Service Default Achieve
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(MessageCenterService.class)
public class MessageCenterServiceDefaultAchieve implements MessageCenterService {

    /**
     * Config Type
     */
    protected static final String CONFIG_TYPE = "CONFIG";

    /**
     * Template Type
     */
    protected static final String TEMPLATE_TYPE = "TEMPLATE";

    /**
     * Launcher Type
     */
    protected static final String LAUNCHER_TYPE = "LAUNCHER";

    /**
     * Default Language
     */
    protected static final String DEFAULT_LANGUAGE = "zh-cn";

    /**
     * Data Source Repository Object
     */
    protected final DataSourceRepository repository;

    /**
     * Launcher Route Service Map Object
     */
    protected final Map<String, LauncherRouteService> launcherRouteServiceMap;

    /**
     * Config Parser Service Map Object
     */
    protected final Map<String, ConfigParserService<?>> configParserServiceMap;

    /**
     * Launcher Template Parser Service Map Object
     */
    protected final Map<String, LauncherTemplateParserService> launcherTemplateParserServiceMap;

    /**
     * Mail Message Launcher Service Map Object
     */
    protected final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap;

    /**
     * Short Message Launcher Service Map Object
     */
    protected final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap;

    /**
     * Mobile Message Launcher Service Map Object
     */
    protected final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap;

    /**
     * WeChat Message Launcher Service Map Object
     */
    protected final Map<String, WeChatMessageLauncherService> wechatMessageLauncherServiceMap;

    /**
     * Telegram Message Launcher Service Map Object
     */
    protected final Map<String, TelegramMessageLauncherService> telegramMessageLauncherServiceMap;

    /**
     * Construct Initialization
     *
     * @param repository                         Data Source Repository Object
     * @param launcherRouteServiceList           Launcher Route Service List Object
     * @param configParserServiceList            Config Parser Service List Object
     * @param launcherTemplateParserServiceList  Launcher Template Parser Service List Object
     * @param mailMessageLauncherServiceList     Mail Message Launcher Service List Object
     * @param shortMessageLauncherServiceList    Short Message Launcher Service List Object
     * @param mobileMessageLauncherServiceList   Mobile Message Launcher Service List Object
     * @param weChatMessageLauncherServiceList   WeChat Message Launcher Service List Object
     * @param telegramMessageLauncherServiceList Telegram Message Launcher Service List Object
     */
    public MessageCenterServiceDefaultAchieve(
            DataSourceRepository repository,
            List<LauncherRouteService> launcherRouteServiceList,
            List<ConfigParserService<?>> configParserServiceList,
            List<LauncherTemplateParserService> launcherTemplateParserServiceList,
            List<MailMessageLauncherService> mailMessageLauncherServiceList,
            List<ShortMessageLauncherService> shortMessageLauncherServiceList,
            List<MobileMessageLauncherService> mobileMessageLauncherServiceList,
            List<WeChatMessageLauncherService> weChatMessageLauncherServiceList,
            List<TelegramMessageLauncherService> telegramMessageLauncherServiceList
    ) {
        final Map<String, LauncherRouteService> launcherRouteServiceMap = new HashMap<>();
        if (launcherRouteServiceList != null
                && !launcherRouteServiceList.isEmpty()) {
            for (final LauncherRouteService service : launcherRouteServiceList) {
                launcherRouteServiceMap.put(service.name(), service);
            }
        }

        final Map<String, ConfigParserService<?>> configParserServiceMap = new HashMap<>();
        if (configParserServiceList != null
                && !configParserServiceList.isEmpty()) {
            for (final ConfigParserService<?> service : configParserServiceList) {
                configParserServiceMap.put(service.name(), service);
            }
        }

        final Map<String, LauncherTemplateParserService> launcherTemplateParserServiceMap = new HashMap<>();
        if (launcherTemplateParserServiceList != null
                && !launcherTemplateParserServiceList.isEmpty()) {
            for (final LauncherTemplateParserService service : launcherTemplateParserServiceList) {
                launcherTemplateParserServiceMap.put(service.name(), service);
            }
        }

        final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap = new HashMap<>();
        if (mailMessageLauncherServiceList != null
                && !mailMessageLauncherServiceList.isEmpty()) {
            for (final MailMessageLauncherService service : mailMessageLauncherServiceList) {
                mailMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap = new HashMap<>();
        if (shortMessageLauncherServiceList != null
                && !shortMessageLauncherServiceList.isEmpty()) {
            for (final ShortMessageLauncherService service : shortMessageLauncherServiceList) {
                shortMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap = new HashMap<>();
        if (mobileMessageLauncherServiceList != null
                && !mobileMessageLauncherServiceList.isEmpty()) {
            for (final MobileMessageLauncherService service : mobileMessageLauncherServiceList) {
                mobileMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, WeChatMessageLauncherService> wechatMessageLauncherServiceMap = new HashMap<>();
        if (mobileMessageLauncherServiceList != null
                && !mobileMessageLauncherServiceList.isEmpty()) {
            for (final WeChatMessageLauncherService service : weChatMessageLauncherServiceList) {
                wechatMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, TelegramMessageLauncherService> telegramMessageLauncherServiceMap = new HashMap<>();
        if (telegramMessageLauncherServiceList != null
                && !telegramMessageLauncherServiceList.isEmpty()) {
            for (final TelegramMessageLauncherService service : telegramMessageLauncherServiceList) {
                telegramMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        this.repository = repository;
        this.configParserServiceMap = configParserServiceMap;
        this.launcherRouteServiceMap = launcherRouteServiceMap;
        this.launcherTemplateParserServiceMap = launcherTemplateParserServiceMap;
        this.mailMessageLauncherServiceMap = mailMessageLauncherServiceMap;
        this.shortMessageLauncherServiceMap = shortMessageLauncherServiceMap;
        this.mobileMessageLauncherServiceMap = mobileMessageLauncherServiceMap;
        this.wechatMessageLauncherServiceMap = wechatMessageLauncherServiceMap;
        this.telegramMessageLauncherServiceMap = telegramMessageLauncherServiceMap;
    }

    /**
     * Get Launcher Data Object
     *
     * @param id Launcher ID
     * @return Launcher Model Object
     */
    protected LauncherModel getLauncherData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final LauncherModel model = ExternalObjectCache.get(LAUNCHER_TYPE, name);
        if (model == null) {
            return setLauncherData(id);
        } else {
            return model;
        }
    }

    /**
     * Set Launcher Data Object
     *
     * @param id Launcher ID
     * @return Launcher Model Object
     */
    protected synchronized LauncherModel setLauncherData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final LauncherModel model = ExternalObjectCache.get(LAUNCHER_TYPE, name);
        if (model == null) {
            final LauncherModel lm = repository.getLauncherData(id);
            if (lm == null) {
                return null;
            } else {
                ExternalObjectCache.set(LAUNCHER_TYPE, name, new ExternalObjectCache.Model(v -> lm));
                return lm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Config Data Object
     *
     * @param id Config ID
     * @return Config Model Object
     */
    protected ConfigModel getConfigData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final ConfigModel model = ExternalObjectCache.get(CONFIG_TYPE, name);
        if (model == null) {
            return setConfigData(id);
        } else {
            return model;
        }
    }

    /**
     * Set Config Data Object
     *
     * @param id Config ID
     * @return Config Model Object
     */
    protected ConfigModel setConfigData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final ConfigModel model = ExternalObjectCache.get(CONFIG_TYPE, name);
        if (model == null) {
            final ConfigModel cm = repository.getConfigData(id);
            if (cm == null) {
                return null;
            } else {
                ExternalObjectCache.set(CONFIG_TYPE, name, new ExternalObjectCache.Model(v -> cm));
                return cm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Template Data Object
     *
     * @param key      Template Key Object
     * @param language Template Language Object
     * @return Template Model Object
     */
    protected TemplateModel getTemplateData(String key, String language) {
        language = (language == null || language.isEmpty()) ? DEFAULT_LANGUAGE : language;
        final String name = Md5Util.execute(Md5Util.execute(key + "@" + language));
        final TemplateModel model = ExternalObjectCache.get(TEMPLATE_TYPE, name);
        if (model == null) {
            return setTemplateData(key, language);
        } else {
            return model;
        }
    }

    /**
     * Set Template Data Object
     *
     * @param key      Template Key Object
     * @param language Template Language Object
     * @return Template Model Object
     */
    protected TemplateModel setTemplateData(String key, String language) {
        language = language == null ? DEFAULT_LANGUAGE : language;
        final String name = Md5Util.execute(Md5Util.execute(key + "@" + language));
        final TemplateModel model = ExternalObjectCache.get(TEMPLATE_TYPE, name);
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(key, language);
            if (tm == null) {
                return null;
            } else {
                ExternalObjectCache.set(TEMPLATE_TYPE, name, new ExternalObjectCache.Model(v -> tm));
                return tm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Launcher Route Service Object
     *
     * @param launcher Launcher Model Object
     * @return Launcher Route Service Object
     */
    protected LauncherRouteService getLauncherRouteService(LauncherModel launcher) {
        final String route = launcher.route();
        if (route.startsWith("classname:")) {
            final byte[] classBytes = launcher.routeSource();
            final String className = route.substring(10);
            return ExternalSourceClassLoader.getInstance().newPackageClassInstance(className, classBytes, LauncherRouteService.class);
        } else {
            return launcherRouteServiceMap.get(route);
        }
    }

    /**
     * Get Config Parser Service Object
     *
     * @param config Config Model Object
     * @return Config Parser Service Object
     */
    protected ConfigParserService<?> getConfigParserService(ConfigModel config) {
        final String parser = config.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = config.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance().newPackageClassInstance(className, classBytes, ConfigParserService.class);
        } else {
            return configParserServiceMap.get(parser);
        }
    }

    /**
     * Get Launcher Template Parser Service Object
     *
     * @param template Template Model Object
     * @return Launcher Template Parser Service Object
     */
    protected LauncherTemplateParserService getLauncherTemplateParserService(TemplateModel template) {
        final String parser = template.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = template.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance().newPackageClassInstance(className, classBytes, LauncherTemplateParserService.class);
        } else {
            return launcherTemplateParserServiceMap.get(parser);
        }
    }

    /**
     * Get Launcher Service Object
     *
     * @param launcher Launcher Model Object
     * @param data     Launcher Service Data Object
     * @return Launcher Service Object
     */
    protected LauncherService<?> getLauncherService(LauncherModel launcher, Map<String, LauncherService<?>> data) {
        final String parser = launcher.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = launcher.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance().newPackageClassInstance(className, classBytes, LauncherService.class);
        } else {
            return data.get(parser);
        }
    }

    /**
     * Get Mail Message Launcher Service Object
     *
     * @param launcher Launcher Model Object
     * @return Mail Message Launcher Service Object
     */
    protected MailMessageLauncherService getMailMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService = getLauncherService(launcher, new HashMap<>(mailMessageLauncherServiceMap));
        if (launcherService instanceof final MailMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNoExistException(
                this.getClass(),
                "fun getMailMessageLauncherService(LauncherModel launcher)",
                "launcher MAIL<" + launcher.parser() + "> service does not exist"
        );
    }

    /**
     * Get Short Message Launcher Service Object
     *
     * @param launcher Launcher Model Object
     * @return Short Message Launcher Service Object
     */
    protected ShortMessageLauncherService getShortMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService = getLauncherService(launcher, new HashMap<>(shortMessageLauncherServiceMap));
        if (launcherService instanceof final ShortMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNoExistException(
                this.getClass(),
                "fun getShortMessageLauncherService(LauncherModel launcher)",
                "launcher SMS<" + launcher.parser() + "> service does not exist"
        );
    }

    /**
     * Get Mobile Message Launcher Service Object
     *
     * @param launcher Launcher Model Object
     * @return Mobile Message Launcher Service Object
     */
    protected MobileMessageLauncherService getMobileMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService = getLauncherService(launcher, new HashMap<>(mobileMessageLauncherServiceMap));
        if (launcherService instanceof final MobileMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNoExistException(
                this.getClass(),
                "fun getMobileMessageLauncherService(LauncherModel launcher).",
                "launcher MOBILE<" + launcher.parser() + "> service does not exist."
        );
    }

    /**
     * Get WeChat Message Launcher Service Object
     *
     * @param launcher WeChat Message Launcher Service Object
     */
    protected WeChatMessageLauncherService getWeChatMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService = getLauncherService(launcher, new HashMap<>(wechatMessageLauncherServiceMap));
        if (launcherService instanceof final WeChatMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNoExistException(
                this.getClass(),
                "fun getWeChatMessageLauncherService(LauncherModel launcher)",
                "launcher WECHAT<" + launcher.parser() + "> service does not exist"
        );
    }

    /**
     * Get Telegram Message Launcher Service Object
     *
     * @param launcher Telegram Message Launcher Service Object
     */
    protected TelegramMessageLauncherService getTelegramMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService = getLauncherService(launcher, new HashMap<>(telegramMessageLauncherServiceMap));
        if (launcherService instanceof final TelegramMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNoExistException(
                this.getClass(),
                "fun getTelegramMessageLauncherService(LauncherModel launcher)",
                "launcher TELEGRAM<" + launcher.parser() + "> service does not exist"
        );
    }

    @Override
    public void cleanCacheData() {
        ExternalObjectCache.clean();
    }

    @Override
    public LauncherResultModel execute(LauncherStartingModel starting) {
        final LauncherModel launcherModel = getLauncherData(starting.id());
        if (launcherModel == null) {
            throw new LauncherNoExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] does not exist"
            );
        }
        if (!launcherModel.enable()) {
            throw new LauncherNoEnableException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] service not enabled"
            );
        }
        final List<ConfigModel> configs = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> launcherConfigs = launcherModel.configs();
        if (launcherConfigs == null || launcherConfigs.isEmpty()) {
            throw new LauncherMapperConfigNoExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] mapper config result list [CONFIG NOT VERIFIED] value is empty or null"
            );
        } else {
            for (final LauncherModel.ConfigMapperModel config : launcherConfigs) {
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    configs.add(cm);
                }
            }
        }
        if (configs.isEmpty()) {
            throw new LauncherMapperConfigNoExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] mapper config result list [CONFIG VERIFIED] value is empty or null"
            );
        }
        final LauncherRouteService launcherRouteService = getLauncherRouteService(launcherModel);
        if (launcherRouteService == null) {
            throw new LauncherRouteConfigException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] route service does not exist"
            );
        }
        final ConfigModel selectConfigModel = launcherRouteService.execute(launcherModel, configs);
        if (selectConfigModel == null) {
            throw new LauncherRouteConfigException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] mapper route config result value is empty or null"
            );
        }
        final ConfigParserService<?> configParserService = getConfigParserService(selectConfigModel);
        if (configParserService == null) {
            throw new LauncherConfigParserException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] config parser service does not exist");
        }
        final ConfigModel finalConfigModel = configParserService.execute(selectConfigModel);
        if (finalConfigModel == null) {
            throw new LauncherConfigParserException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] config parser service execute config value is empty or null"
            );
        }
        final TemplateModel finalTemplateModel = getTemplateData(launcherModel.template(), starting.language());
        if (finalTemplateModel == null) {
            throw new LauncherTemplateNoExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] mapper template(" + launcherModel.template() + "/" + starting.language() + ") value is empty or null");
        }
        final LauncherTemplateParserService launcherTemplateParserService = getLauncherTemplateParserService(finalTemplateModel);
        if (launcherTemplateParserService == null) {
            throw new LauncherTemplateParserException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] launcher template parser service does not exist");
        }
        final LauncherTemplateModel finalLauncherTemplateModel = launcherTemplateParserService.execute(starting, finalTemplateModel);
        if (finalLauncherTemplateModel == null) {
            throw new LauncherTemplateParserException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting)",
                    "launcher [" + starting.id() + "] mapper launcher template value is empty or null");
        }
        return execute(launcherModel, finalConfigModel, finalLauncherTemplateModel);
    }

    /**
     * Execute Push Message
     *
     * @param configModel           Config Model Object
     * @param launcherModel         Launcher Model Object
     * @param launcherTemplateModel Launcher Template Model Object
     * @return Launcher Result Model Object
     */
    protected LauncherResultModel execute(LauncherModel launcherModel, ConfigModel configModel, LauncherTemplateModel launcherTemplateModel) {
        switch (launcherModel.type()) {
            case SMS:
                if (configModel instanceof final ShortMessageConfigModel shortMessageConfigModel) {
                    return getShortMessageLauncherService(launcherModel).execute(launcherTemplateModel, shortMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting)",
                            "launcher [" + launcherModel.id() + "] SMS >>> " + ShortMessageConfigModel.class + " ::: " + configModel.getClass() + " type mismatch exception");
                }
            case MAIL:
                if (configModel instanceof final MailMessageConfigModel mailMessageConfigModel) {
                    return getMailMessageLauncherService(launcherModel).execute(launcherTemplateModel, mailMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting)",
                            "launcher [" + launcherModel.id() + "] MAIL >>> " + MailMessageConfigModel.class + " ::: " + configModel.getClass() + " type mismatch exception"
                    );
                }
            case MOBILE:
                if (configModel instanceof final MobileMessageConfigModel mobileMessageConfigModel) {
                    return getMobileMessageLauncherService(launcherModel).execute(launcherTemplateModel, mobileMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting)",
                            "launcher [" + launcherModel.id() + "] MOBILE >>> " + MobileMessageConfigModel.class + " ::: " + configModel.getClass() + " type mismatch exception"
                    );
                }
            case WECHAT:
                if (configModel instanceof final WeChatMessageConfigModel weChatMessageConfigModel) {
                    return getWeChatMessageLauncherService(launcherModel).execute(launcherTemplateModel, weChatMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting)",
                            "launcher [" + launcherModel.id() + "] WECHAT >>> " + WeChatMessageConfigModel.class + " ::: " + configModel.getClass() + " type mismatch exception"
                    );
                }
            case TELEGRAM:
                if (configModel instanceof final TelegramMessageConfigModel templateMessageModel) {
                    return getTelegramMessageLauncherService(launcherModel).execute(launcherTemplateModel, templateMessageModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "launcher [" + launcherModel.id() + "]" + " TELEGRAM >>> " + TelegramMessageConfigModel.class + " ::: " + configModel.getClass() + " type mismatch exception"
                    );
                }
            default:
                throw new LauncherConfigTypeMismatchException(
                        this.getClass(),
                        "fun execute(LauncherStartingModel starting).",
                        "launcher [" + launcherModel.id() + "] TYPE >>> " + launcherModel.type().name() + " ::: SMS/MAIL/MOBILE type mismatch exception"
                );
        }
    }

}
