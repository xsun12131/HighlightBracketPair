package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import io.github.qeesung.adapter.BraceMatchingUtil;
import io.github.qeesung.brace.Brace;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.brace.BraceTokenTypes;
import io.github.qeesung.plugins.XmlSupportedToken;
import io.github.qeesung.setting.HighlightBracketPairSettingsPage;
import io.github.qeesung.util.Pair;

import java.util.LinkedList;
import java.util.List;

import static io.github.qeesung.brace.BraceTokenTypes.DOUBLE_QUOTE;

/**
 * Brace highlighter abstract class.
 */
abstract public class BraceHighlighter {
    public final static int NON_OFFSET = -1;
    public final static int HIGHLIGHT_LAYER_WEIGHT = 100;
    public final static BracePair EMPTY_BRACE_PAIR =
            new BracePair.BracePairBuilder().
                    leftOffset(NON_OFFSET).
                    rightOffset(NON_OFFSET).build();

    protected Editor editor;
    protected Project project;
    protected Document document;
    protected FileType fileType;
    protected CharSequence fileText;
    protected PsiFile psiFile;
    protected MarkupModelEx markupModelEx;

    public BraceHighlighter(Editor editor) {
        this.editor = editor;
        this.project = this.editor.getProject();
        this.document = this.editor.getDocument();
        this.psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        this.fileType = psiFile.getFileType();
        this.fileText = this.editor.getDocument().getImmutableCharSequence();
        this.markupModelEx = (MarkupModelEx) this.editor.getMarkupModel();
    }

    /**
     * @return
     */
    public List<Pair<IElementType, IElementType>> getSupportedBraceToken() {
        return new LinkedList<>();
    }

    public BracePair findClosetBracePairInBraceTokens(int offset) {
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        boolean isBlockCaret = this.isBlockCaret();
        List<Pair<IElementType, IElementType>> braceTokens = this.getSupportedBraceToken();
        for (Pair<IElementType, IElementType> braceTokenPair :
                braceTokens) {
            HighlighterIterator leftTraverseIterator = editorHighlighter.createIterator(offset);
            HighlighterIterator rightTraverseIterator = editorHighlighter.createIterator(offset);

            int leftBraceOffset = BraceMatchingUtil.findLeftLParen(
                    leftTraverseIterator, braceTokenPair.getLeft(), this.fileText, this.fileType, isBlockCaret, offset);
            int rightBraceOffset = BraceMatchingUtil.findRightRParen(
                    rightTraverseIterator, braceTokenPair.getRight(), this.fileText, this.fileType, isBlockCaret, offset);
            if (leftBraceOffset != NON_OFFSET && rightBraceOffset != NON_OFFSET) {
                if (braceTokenPair.getRight().equals(XmlTokenType.XML_TAG_END)) {
                    HighlighterIterator leftIterator = editorHighlighter.createIterator(leftBraceOffset);
                    HighlighterIterator rightIterator = editorHighlighter.createIterator(rightBraceOffset);
                    String leftText = XmlSupportedToken.getLeftPartOnlyName(leftBraceOffset, leftIterator);
                    String rightText = XmlSupportedToken.getRightPart(rightBraceOffset + 1, rightIterator, leftBraceOffset);
                    return new BracePair.BracePairBuilder().
                            leftType(braceTokenPair.getLeft()).
                            rightType(braceTokenPair.getRight()).
                            leftText(leftText)
                            .rightText(rightText).
                            leftOffset(leftBraceOffset).
                            rightOffset(rightBraceOffset - rightText.length() + 1).build();
                }
                if (braceTokenPair.getRight().equals(XmlTokenType.XML_EMPTY_ELEMENT_END)) {
                    HighlighterIterator leftIterator = editorHighlighter.createIterator(leftBraceOffset);
                    String leftText = XmlSupportedToken.getLeftPartOnlyName(leftBraceOffset, leftIterator);
                    String rightText = document.getText(new TextRange(rightTraverseIterator.getStart(),
                            rightTraverseIterator.getEnd()));
                    return new BracePair.BracePairBuilder().
                            leftType(braceTokenPair.getLeft()).
                            rightType(braceTokenPair.getRight()).
                            leftText(leftText).
                            rightText(rightText).
                            leftOffset(leftBraceOffset).
                            rightOffset(rightTraverseIterator.getStart())
                            .build();
                }
                if (braceTokenPair.getRight().equals(BraceTokenTypes.TEXT_TOKEN)) {
                    String leftText = document.getText(new TextRange(leftBraceOffset, leftBraceOffset + 1));
                    String rightText = document.getText(new TextRange(rightBraceOffset, rightBraceOffset + 1));
                    return new BracePair.BracePairBuilder().
                            leftType(braceTokenPair.getLeft()).
                            rightType(braceTokenPair.getRight()).
                            leftText(leftText).
                            rightText(rightText).
                            leftOffset(leftBraceOffset).
                            rightOffset(rightBraceOffset)
                            .build();
                }
                return new BracePair.BracePairBuilder().
                        leftType(braceTokenPair.getLeft()).
                        rightType(braceTokenPair.getRight()).
                        leftIterator(leftTraverseIterator).
                        rightIterator(rightTraverseIterator).build();


            }
        }
        return EMPTY_BRACE_PAIR;
    }

    public BracePair findClosetBracePairInStringSymbols(int offset) {
        if (offset < 0 || this.fileText == null || this.fileText.length() == 0)
            return EMPTY_BRACE_PAIR;
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        HighlighterIterator iterator = editorHighlighter.createIterator(offset);
        IElementType type = iterator.getTokenType();
        boolean isBlockCaret = this.isBlockCaret();
        if (!BraceMatchingUtil.isStringToken(type))
            return EMPTY_BRACE_PAIR;

        int leftOffset = iterator.getStart();
        int rightOffset = iterator.getEnd() - 1;
        if (!isBlockCaret && leftOffset == offset)
            return EMPTY_BRACE_PAIR;
        return new BracePair.BracePairBuilder().
                leftType(DOUBLE_QUOTE).
                rightType(DOUBLE_QUOTE).
                leftOffset(leftOffset).
                rightOffset(rightOffset).build();
    }

    /**
     * 查找符号对
     * @param offset
     * @return
     */
    public BracePair findClosetBracePair(int offset) {
        BracePair braceTokenBracePair = this.findClosetBracePairInBraceTokens(offset);
        BracePair stringSymbolBracePair = this.findClosetBracePairInStringSymbols(offset);
        if (
                (offset - braceTokenBracePair.getLeftBrace().getOffset() >
                        offset - stringSymbolBracePair.getLeftBrace().getOffset())
                        && (offset - braceTokenBracePair.getRightBrace().getOffset() <
                        offset - stringSymbolBracePair.getRightBrace().getOffset()
                )) {
            return stringSymbolBracePair;
        } else {
            return braceTokenBracePair;
        }
    }

    /**
     * 渲染符号对
     * @param bracePair
     * @return
     */
    public Pair<RangeHighlighter, RangeHighlighter> highlightPair(BracePair bracePair) {
        final Brace leftBrace = bracePair.getLeftBrace();
        final Brace rightBrace = bracePair.getRightBrace();
        final int leftBraceOffset = leftBrace.getOffset();
        final int rightBraceOffset = rightBrace.getOffset();
        final String leftBraceText = leftBrace.getText();
        final String rightBraceText = rightBrace.getText();

        if (leftBraceOffset == NON_OFFSET ||
                rightBraceOffset == NON_OFFSET)
            return null;
        // try to get the text attr by element type
        TextAttributesKey textAttributesKey =
                HighlightBracketPairSettingsPage.getTextAttributesKeyByToken(leftBrace.getElementType());
        // if not found, get the text attr by brace text
        if (textAttributesKey == null) {
            textAttributesKey = HighlightBracketPairSettingsPage.getTextAttributesKeyByText(leftBraceText);
        }
        final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(textAttributesKey);

        RangeHighlighter leftHighlighter = markupModelEx.addRangeHighlighter(
                leftBraceOffset,
                leftBraceOffset + leftBraceText.length(),
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        RangeHighlighter rightHighlighter = markupModelEx.addRangeHighlighter(
                rightBraceOffset,
                rightBraceOffset + rightBraceText.length(),
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        return new Pair<>(leftHighlighter, rightHighlighter);
    }

    /**
     * 清除渲染
     * @param list
     */
    public void eraseHighlight(List<RangeHighlighter> list) {
        for (RangeHighlighter l :
                list) {
            this.markupModelEx.removeHighlighter(l);
        }
    }

    public boolean isBlockCaret() {
        return this.editor.getSettings().isBlockCursor();
    }
}
