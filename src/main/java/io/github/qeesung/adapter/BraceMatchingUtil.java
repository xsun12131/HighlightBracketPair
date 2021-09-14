package io.github.qeesung.adapter;

import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;

import java.util.*;

import static io.github.qeesung.brace.BraceTokenTypes.*;

public class BraceMatchingUtil {

    public static final int NON_BRACE_OFFSET = -1;

    public static final Set<String> STRING_TOKEN_SET = new HashSet<>();

    public static final BraceMatchingFactory braceMatchingFactory = BraceMatchingFactory.getInstance();

    private static final Map<Character, Character> BRACKETS = new HashMap<>();

    private static final char L_BRACE_ATTR = '{';
    private static final char R_BRACE_ATTR = '}';

    private static final char L_BRACKET_ATTR = '[';
    private static final char R_BRACKET_ATTR = ']';

    private static final char L_PARENTHESIS_ATTR = '(';
    private static final char R_PARENTHESIS_ATTR = ')';

    private static final char L_CUSP_BRACKETS_ATTR = '<';
    private static final char R_CUSP_BRACKETS_ATTR = '>';


    static {
        STRING_TOKEN_SET.add(GROOVY_STRING_TOKEN);
        STRING_TOKEN_SET.add(GROOVY_SINGLE_QUOTE_TOKEN);
        STRING_TOKEN_SET.add(KOTLIN_STRING_TOKEN);
        STRING_TOKEN_SET.add(KOTLIN_CHAR_TOKEN);
        STRING_TOKEN_SET.add(JS_STRING_TOKEN);
        STRING_TOKEN_SET.add(JAVA_STRING_TOKEN);
        STRING_TOKEN_SET.add(SCALA_STRING_TOKEN);
        STRING_TOKEN_SET.add(HASKELL_STRING_TOKEN);

        BRACKETS.put(L_BRACE_ATTR, R_BRACE_ATTR);
        BRACKETS.put(L_BRACKET_ATTR, R_BRACKET_ATTR);
        BRACKETS.put(L_PARENTHESIS_ATTR, R_PARENTHESIS_ATTR);
        BRACKETS.put(L_CUSP_BRACKETS_ATTR, R_CUSP_BRACKETS_ATTR);
    }

    /**
     * check is the current token type is string token.
     *
     * @param tokenType token type
     * @return is string token
     */
    public static boolean isStringToken(IElementType tokenType) {
        String elementName = tokenType.toString();
        return STRING_TOKEN_SET.contains(elementName);
    }

    /**
     * Find the left closest brace offset position.
     *
     * @param iterator        highlighter iterator
     * @param lparenTokenType left token type to be paired
     * @param fileText        file text
     * @param fileType        file type
     * @return offset
     */
    public static int findLeftLParen(HighlighterIterator iterator,
                                     IElementType lparenTokenType,
                                     CharSequence fileText,
                                     FileType fileType, boolean isBlockCaret, int offset) {
        return braceMatchingFactory.get(fileType).findLeftLParen(iterator, lparenTokenType, fileText, fileType, isBlockCaret, offset);
    }

    /**
     * find the right closest brace offset position
     *
     * @param iterator        highlight iterator
     * @param rparenTokenType right token type to paired
     * @param fileText        file text
     * @param fileType        file type
     * @return offset
     */
    public static int findRightRParen(HighlighterIterator iterator,
                                      IElementType rparenTokenType,
                                      CharSequence fileText,
                                      FileType fileType, boolean isBlockCaret, int offset) {

        return braceMatchingFactory.get(fileType).findRightRParen(iterator, rparenTokenType, fileText, fileType, isBlockCaret, offset);
    }

    /**
     * 查找字符串符号匹配
     *
     * @param fileText   字符文本
     * @param offset     查找偏移量
     * @param rightParen 右符号
     * @return
     */
    public static int findLeftLParen(CharSequence fileText, int offset, Character rightParen) {
        if (offset > fileText.length() || offset < 0) {
            return NON_BRACE_OFFSET;
        }
        Stack<Integer> index = new Stack<>();
        for (int i = offset - 1; i > -1; i--) {
            char c = fileText.charAt(i);
            if (isLParen(c)) {
                if (!index.isEmpty()) {
                    index.pop();
                    continue;
                }
                return i;
            } else if (isRParen(c)) {
                index.push(i);
            }
        }
        return NON_BRACE_OFFSET;
    }

    public static int findRightRParen(CharSequence fileText, int offset, Character leftParen) {
        if (offset > fileText.length() || offset < 0) {
            return NON_BRACE_OFFSET;
        }
        Stack<Integer> index = new Stack<>();
        for (int i = offset; i < fileText.length(); i++) {
            char c = fileText.charAt(i);
            if (isRParen(c)) {
                if (!index.isEmpty()) {
                    index.pop();
                    continue;
                }
                return i;
            } else if (isLParen(c)) {
                index.push(i);
            }
        }
        return NON_BRACE_OFFSET;
    }

    private static boolean isLParen(char c) {
        return c == L_BRACE_ATTR || c == L_BRACKET_ATTR || c == L_CUSP_BRACKETS_ATTR || c == L_PARENTHESIS_ATTR;
    }

    private static boolean isRParen(char c) {
        return c == R_BRACE_ATTR || c == R_BRACKET_ATTR || c == R_CUSP_BRACKETS_ATTR || c == R_PARENTHESIS_ATTR;
    }

}
