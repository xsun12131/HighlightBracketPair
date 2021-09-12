package io.github.qeesung.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.xml.IXmlLeafElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.xml.impl.XmlBraceMatcher;
import io.github.qeesung.plugins.VueSupportedToken;
import io.github.qeesung.plugins.XmlSupportedToken;
import io.github.qeesung.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default Brace Highlighter to highlight all supported brace pair.
 */
public class DefaultBraceHighlighter extends BraceHighlighter {
    public static Map<Language, List<Pair<IElementType, IElementType>>>
            LanguageBracePairs = new ConcurrentHashMap<>();

    /**
     * Get all the registered languages' brace pairs and cache it.
     */
    static {
        refresh();
    }


    /**
     * Constructor.
     *
     * @param editor editor
     */
    public DefaultBraceHighlighter(Editor editor) {
        super(editor);
    }

    /**
     * Get all cached supported brace token pair.
     *
     * @return all supported brace pair token
     */
    @Override
    public List<Pair<IElementType, IElementType>> getSupportedBraceToken() {
        Language language = this.psiFile.getLanguage();
        List<Pair<IElementType, IElementType>> braceList = LanguageBracePairs.get(language);
        return braceList == null ? customSupportedBraceToken(language) : braceList;
    }

    private List<Pair<IElementType, IElementType>> customSupportedBraceToken(Language language) {
        refresh();
        XmlSupportedToken.Singleton.INSTANCE.addSupported(LanguageBracePairs);
        VueSupportedToken.Singleton.INSTANCE.addSupported(LanguageBracePairs);
        List<Pair<IElementType, IElementType>> braceList = LanguageBracePairs.get(language);
        return braceList == null ? super.getSupportedBraceToken() : braceList;
    }

    private static void refresh () {
        Collection<Language> languageList = Language.getRegisteredLanguages();
        for (Language language :
                languageList) {
            PairedBraceMatcher pairedBraceMatcher =
                    LanguageBraceMatching.INSTANCE.forLanguage(language);
            if (pairedBraceMatcher != null) {
                BracePair[] bracePairs =
                        pairedBraceMatcher.getPairs();
                List<Pair<IElementType, IElementType>> braceList
                        = new LinkedList<>();
                if (bracePairs != null) {
                    for (BracePair bracePair :
                            bracePairs) {
                        Pair<IElementType, IElementType> braceEntry =
                                new Pair<>(
                                        bracePair.getLeftBraceType(),
                                        bracePair.getRightBraceType()
                                );
                        braceList.add(braceEntry);
                    }
                }
                LanguageBracePairs.put(language, braceList);
            }
        }
    }

}
