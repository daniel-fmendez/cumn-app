package com.example.entrega1;

import java.util.HashMap;
import java.util.Map;

public class LanguageTranslator {
    public static final Map<String, String> languageMap = new HashMap<>();

    static {
        languageMap.put("eng", "Inglés");
        languageMap.put("spa", "Español");
        languageMap.put("fra", "Francés");
        languageMap.put("bel", "Bielorruso");
        languageMap.put("ita", "Italiano");
        languageMap.put("deu", "Alemán");
    }

    public static String getLanguage(String code) {
        return languageMap.getOrDefault(code, "Desconocido");
    }
}