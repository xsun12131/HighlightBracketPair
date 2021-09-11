package io.github.qeesung.plugins;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VueSupportedToken implements CustomSupportedToken {

    @Override
    public Map<Language, List<Pair<IElementType, IElementType>>> addSupported(Map<Language, List<Pair<IElementType, IElementType>>> map) {
        Language vue = Language.findLanguageByID("Vue");
        List<Pair<IElementType, IElementType>> vueJsPairList = map.get(Language.findLanguageByID("VueJS"));
        List<Pair<IElementType, IElementType>> xmlPairList = map.get(Language.findLanguageByID("XML"));
        List<Pair<IElementType, IElementType>> cssPairList = map.get(Language.findLanguageByID("CSS"));
        List<Pair<IElementType, IElementType>> vuePairList = new ArrayList<>();
        if(null!=vueJsPairList) vuePairList.addAll(vueJsPairList);
        if(null!=xmlPairList) vuePairList.addAll(xmlPairList);
        if(null!=cssPairList) vuePairList.addAll(cssPairList);
        map.put(vue, vuePairList);
        return map;
    }
}
