package io.github.qeesung.adapter;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;

import java.util.HashMap;
import java.util.Map;

public class BraceMatchingFactory {

    Map<String, BraceMatching> braceMatchingMap = new HashMap<>();

    private BraceMatchingFactory() {
        braceMatchingMap.put(null, new BraceMatching() {
        });
        init();
    }

    public void addBraceMatching(FileType fileType, BraceMatching braceMatching) {
        braceMatchingMap.put(fileType.getName(), braceMatching);
    }

    public BraceMatching get(FileType fileType) {
        BraceMatching braceMatching = braceMatchingMap.get(fileType.getName());
        return braceMatching == null ? braceMatchingMap.get(null) : braceMatching;
    }

    private void init() {
        braceMatchingMap.put(XmlFileType.INSTANCE.getName(), new XmlBraceMatching());
        braceMatchingMap.put("Vue.js", new XmlBraceMatching());
    };

    public static BraceMatchingFactory getInstance() {
        return BraceMatchingFactory.SingletonInstance.INSTANCE.get();
    }

    private enum SingletonInstance {
        INSTANCE;

        private BraceMatchingFactory braceMatchingFactory;

        SingletonInstance() {
            braceMatchingFactory = new BraceMatchingFactory();
        }

        BraceMatchingFactory get() {
            return braceMatchingFactory;
        }
    }


}
