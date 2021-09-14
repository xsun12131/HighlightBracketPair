package io.github.qeesung.adapter;

import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import io.github.qeesung.brace.BraceTokenTypes;

import java.util.Stack;

public class XmlBraceMatching implements BraceMatching {

    @Override
    public int findLeftLParen(HighlighterIterator iterator, IElementType lparenTokenType, CharSequence fileText, FileType fileType, boolean isBlockCaret, int offset) {
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        if(iterator.getTokenType().equals(XmlTokenType.XML_DATA_CHARACTERS)) {
            int leftLParen = io.github.qeesung.adapter.BraceMatchingUtil
                    .findLeftLParen(fileText, offset, null);
            if (leftLParen != NON_BRACE_OFFSET && lparenTokenType == BraceTokenTypes.TEXT_TOKEN) {
                return leftLParen;
            }
            return NON_BRACE_OFFSET;
        }
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

    @Override
    public int findRightRParen(HighlighterIterator iterator, IElementType rparenTokenType, CharSequence fileText, FileType fileType, boolean isBlockCaret, int offset) {
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        if(iterator.getTokenType().equals(XmlTokenType.XML_DATA_CHARACTERS)) {
            int rightLParen = io.github.qeesung.adapter.BraceMatchingUtil
                    .findRightRParen(fileText, offset, null);
            if (rightLParen != NON_BRACE_OFFSET && rparenTokenType == BraceTokenTypes.TEXT_TOKEN) {
                return rightLParen;
            }
            return NON_BRACE_OFFSET;
        }
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

    boolean isLBraceToken(HighlighterIterator iterator, CharSequence fileText, FileType fileType) {
        return BraceMatchingUtil.isLBraceToken(iterator, fileText, fileType);
    }

    boolean isRBraceToken(HighlighterIterator iterator, CharSequence fileText, FileType fileType) {
        return BraceMatchingUtil.isRBraceToken(iterator, fileText, fileType);
    }

    boolean isPairBraces(IElementType tokenType1, IElementType tokenType2, FileType fileType) {
        return BraceMatchingUtil.isPairBraces(tokenType1, tokenType2, fileType);
    }

}
