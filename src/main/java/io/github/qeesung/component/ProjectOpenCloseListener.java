package io.github.qeesung.component;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class ProjectOpenCloseListener implements ProjectManagerListener {


    @Override
    public void projectOpened(@NotNull Project project) {
        HighlightBracketPairService highlightBracketPairService
                = ApplicationManager.getApplication().getService(HighlightBracketPairService.class);
        highlightBracketPairService.init();
        highlightBracketPairService.updateCheck(project);

    }

    @Override
    public void projectClosed(@NotNull Project project) {
        HighlightBracketPairService highlightBracketPairService
                = ApplicationManager.getApplication().getService(HighlightBracketPairService.class);
        highlightBracketPairService.disposeComponent();
    }
}
