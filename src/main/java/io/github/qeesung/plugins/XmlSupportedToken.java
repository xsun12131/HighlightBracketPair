package io.github.qeesung.plugins;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import io.github.qeesung.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlSupportedToken implements CustomSupportedToken {

    public static String getLeftPart(int start, HighlighterIterator iterator) {
        Document document = iterator.getDocument();
        for (; !iterator.atEnd(); iterator.advance()) {
            IElementType tokenType = iterator.getTokenType();
            if (tokenType == XmlTokenType.XML_TAG_END && iterator.getEnd() > start) {
                return document.getText(new TextRange(start,
                        iterator.getEnd()));
            }
        }
        return "";
    }

    public static String getRightPart(int end, HighlighterIterator iterator) {
        Document document = iterator.getDocument();
        for (; !iterator.atEnd(); iterator.retreat()) {
            IElementType tokenType = iterator.getTokenType();
            if (tokenType == XmlTokenType.XML_END_TAG_START) {
                return document.getText(new TextRange(iterator.getStart(),
                        end));
            }
        }
        return "";
    }

    @Override
    public Map<Language, List<Pair<IElementType, IElementType>>> addSupported(Map<Language, List<Pair<IElementType, IElementType>>> map) {
        Language xml = Language.findLanguageByID("XML");
        List<Pair<IElementType, IElementType>> pairList = map.get(xml);
        if (pairList == null) {
            pairList = new ArrayList<>();
            pairList.add(new Pair<>(XmlTokenType.XML_START_TAG_START, XmlTokenType.XML_TAG_END));
            pairList.add(new Pair<>(XmlTokenType.XML_START_TAG_START, XmlTokenType.XML_EMPTY_ELEMENT_END));
            map.put(xml, pairList);
        }
        return map;
    }
}
