package io.github.qeesung.component;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HighlightBracketPairEditorFactoryListener implements EditorFactoryListener {

    Map<Editor, HighlightEditorListener> editorHighlightEditorListenerMap = new HashMap<>();

    /**
     * Invoked when the editor is created, and establish the relationship
     * between the {@link Editor} editor and {@link HighlightEditorListener} component.
     *
     * @param editorFactoryEvent editor factory event.
     */
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        HighlightEditorListener highlightEditorListener =
                new HighlightEditorListener(editor);
        editorHighlightEditorListenerMap.put(editor, highlightEditorListener);
    }

    /**
     * Invoked when the editor is released, and dissolve the relationship
     * between the {@link Editor} editor and {@link HighlightEditorListener} component,
     * and dispose the component.
     *
     * @param editorFactoryEvent
     */
    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        HighlightEditorListener highlightEditorListener = editorHighlightEditorListenerMap.get(editor);
        if (highlightEditorListener != null) {
            highlightEditorListener.dispose();
            editorHighlightEditorListenerMap.remove(editor);
        }
    }
}
