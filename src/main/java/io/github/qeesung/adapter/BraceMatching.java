package io.github.qeesung.adapter;

import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.codeInsight.highlighting.BraceMatchingUtil.*;

public interface BraceMatching {

    int NON_BRACE_OFFSET = -1;

    default int findLeftLParen(HighlighterIterator iterator,
                               IElementType lparenTokenType,
                               CharSequence fileText,
                               FileType fileType, boolean isBlockCaret, int offset) {
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        for (; !iterator.atEnd(); iterator.retreat()) {
            final IElementType tokenType = iterator.getTokenType();
            if (isLBraceToken(iterator, fileText, fileType)) {
                if (!isBlockCaret && initOffset == iterator.getStart())
                    continue;
                if (!braceStack.isEmpty()) {
                    IElementType topToken = braceStack.pop();
                    if (!isPairBraces(tokenType, topToken, fileType)) {
                        break; // unmatched braces
                    }
                } else {
                    if (tokenType == lparenTokenType) {
                        return iterator.getStart();
                    } else {
                        break;
                    }
                }
            } else if (isRBraceToken(iterator, fileText, fileType)) {
                if (initOffset == iterator.getStart())
                    continue;
                braceStack.push(iterator.getTokenType());
            }
        }

        return NON_BRACE_OFFSET;
    }

    default int findRightRParen(HighlighterIterator iterator,
                                IElementType rparenTokenType,
                                CharSequence fileText,
                                FileType fileType, boolean isBlockCaret, int offset) {
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        for (; !iterator.atEnd(); iterator.advance()) {
            final IElementType tokenType = iterator.getTokenType();
            if (isRBraceToken(iterator, fileText, fileType)) {
                if (!braceStack.isEmpty()) {
                    IElementType topToken = braceStack.pop();
                    if (!isPairBraces(tokenType, topToken, fileType)) {
                        break; // unmatched braces
                    }
                } else {
                    if (tokenType == rparenTokenType) {
                        return iterator.getStart();
                    } else {
                        break;
                    }
                }
            } else if (isLBraceToken(iterator, fileText, fileType)) {
                if (isBlockCaret && initOffset == iterator.getStart())
                    continue;
                else
                    braceStack.push(iterator.getTokenType());
            }
        }

        return NON_BRACE_OFFSET;
    }

}
