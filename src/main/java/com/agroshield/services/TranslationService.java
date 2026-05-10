package com.agroshield.services;
import java.util.HashMap;
import java.util.Map;

public class TranslationService {
    private String currentLanguage = "en";
    private Map<String, Map<String, String>> dictionary = new HashMap<>();

    public TranslationService() {
        Map<String, String> en = new HashMap<>();
        en.put("login", "Login"); en.put("register", "Register");
        en.put("dashboard", "Dashboard"); en.put("scan", "Disease Scanner");

        Map<String, String> hi = new HashMap<>();
        hi.put("login", "लॉग इन करें"); hi.put("register", "रजिस्टर करें");
        hi.put("dashboard", "डैशबोर्ड"); hi.put("scan", "रोग स्कैनर");

        Map<String, String> pa = new HashMap<>();
        pa.put("login", "ਲਾਗਿਨ"); pa.put("register", "ਰਜਿਸਟਰ");
        pa.put("dashboard", "ਡੈਸ਼ਬੋਰਡ"); pa.put("scan", "ਬਿਮਾਰੀ ਸਕੈਨਰ");

        dictionary.put("en", en); dictionary.put("hi", hi); dictionary.put("pa", pa);
    }

    public void setLanguage(String lang) { currentLanguage = lang; }
    public String translate(String key) {
        return dictionary.getOrDefault(currentLanguage, dictionary.get("en")).getOrDefault(key, key);
    }
}