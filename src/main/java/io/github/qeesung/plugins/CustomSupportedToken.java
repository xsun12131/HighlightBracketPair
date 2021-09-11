package io.github.qeesung.plugins;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CustomSupportedToken {

    Map<Language, List<Pair<IElementType, IElementType>>> addSupported(Map<Language, List<Pair<IElementType, IElementType>>> map);

}
