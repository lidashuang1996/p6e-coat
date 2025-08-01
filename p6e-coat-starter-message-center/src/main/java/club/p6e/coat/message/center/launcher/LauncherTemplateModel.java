package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.template.TemplateModel;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Launcher Template Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherTemplateModel extends TemplateModel, Serializable {

    /**
     * Get Chat
     *
     * @return Chat
     */
    String getChat();

    /**
     * Get Type
     *
     * @return Type
     */
    String getType();

    /**
     * Get Language
     *
     * @return Language
     */
    String getLanguage();

    /**
     * Set Language
     *
     * @param language Language
     */
    void setLanguage(String language);

    /**
     * Get Recipient List Object
     *
     * @return Recipient List Object
     */
    List<String> getRecipients();

    /**
     * Set Recipient List Object
     *
     * @param recipients Recipient List Object
     */
    void setRecipients(List<String> recipients);

    /**
     * Get Message Param
     *
     * @return Message Param
     */
    Map<String, String> getMessageParam();

    /**
     * Set Message Param
     *
     * @param param Message Param
     */
    void setMessageParam(Map<String, String> param);

    /**
     * Get Message Title
     *
     * @return Message Title
     */
    String getMessageTitle();

    /**
     * Set Message Title
     *
     * @param title Message Title
     */
    void setMessageTitle(String title);

    /**
     * Get Message Content
     *
     * @return Message Content
     */
    String getMessageContent();

    /**
     * Set Message Content
     *
     * @param content Message Content
     */
    void setMessageContent(String content);

    /**
     * Get Attachment
     *
     * @return Attachment
     */
    List<File> getAttachment();

    /**
     * Set Attachment
     *
     * @param files Attachment List
     */
    void setAttachment(List<File> files);

    /**
     * Clean Attachment
     */
    void cleanAttachment();

    /**
     * Add Attachment
     *
     * @param file Attachment
     */
    void addAttachment(File file);

    /**
     * Delete Attachment
     *
     * @param index Delete Index
     */
    void removeAttachmentAt(int index);

}
