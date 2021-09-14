package io.github.qeesung.component;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import io.github.qeesung.setting.HighlightBracketPairSettings;
import org.jetbrains.annotations.NotNull;

public class HighlightBracketPairService {

    /**
     * Plugin has been updated with the current run.
     */
    private boolean updated;
    /**
     * Plugin update notification has been shown.
     */
    private boolean updateNotificationShown;

    /**
     * Get the {@link HighlightBracketPairService} component single instance.
     *
     * @return the {@link HighlightBracketPairService} single instance.
     */
    public static HighlightBracketPairService getInstance() {
        return ApplicationManager.getApplication().getService(HighlightBracketPairService.class);
    }

    /**
     * Checks if plugin was updated in the current run.
     *
     * @return plugin was updated
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Check if the plugin notification is shown.
     *
     * @return is shown.
     */
    public boolean isUpdateNotificationShown() {
        return updateNotificationShown;
    }

    /**
     * Update the state that is notification is shown.
     *
     * @param shown new shown state
     */
    public void setUpdateNotificationShown(boolean shown) {
        this.updateNotificationShown = shown;
    }


    /**
     * Invoked when the application is started, then register the {@link HighlightBracketPairService}
     * component to the editor events,  and check if the plugin is updated.
     */
    public void init() {
        final HighlightBracketPairSettings settings = HighlightBracketPairSettings.getInstance();
        updated = !getPlugin().getVersion().equals(settings.getVersion());
        if (updated) {
            settings.setVersion(getPlugin().getVersion());
        }
    }

    public void updateCheck(Project project) {
        if (this.updated && !this.isUpdateNotificationShown()) {
            this.setUpdateNotificationShown(true);
            NotificationGroupManager.getInstance().getNotificationGroup("HighlightBracket update")
                    .createNotification(
                            "HighlightBracketPair is updated to "
                                    + HighlightBracketPairSettings.getInstance().getVersion(),
                            "<br/>If this plugin helps you, please give me a star on " +
                                    "<b><a href=\"https://github.com/xsun12131/HighlightBracketPair\">Github</a>, ^_^.</b>",
                            NotificationType.INFORMATION)
                    .notify(project);
        }
    }

    /**
     * Invoked when the application is shutdown, then dissolve all the relationship between the
     * {@link Editor } editors and {@link HighlightEditorListener} components, finally dispose
     * all the components.
     */
    public void disposeComponent() {
    }

    /**
     * Get the component name.
     *
     * @return component name
     */
    @NotNull
    public String getComponentName() {
        return "HighlightBracketPair";
    }

    /**
     * Get the plugin description by plugin id.
     *
     * @return plugin description
     */
    private IdeaPluginDescriptor getPlugin() {
        return PluginManagerCore.getPlugin(
                PluginId.getId("io.github.qeesung.component.HighlightBracketPair"));
    }

}
