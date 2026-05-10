package com.agroshield.chatbot;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatbotService {
    private Map<String, Function<String, String>> rules = new HashMap<>();

    public ChatbotService() {
        rules.put("hello", s -> "Hello! I am AgriBot. How can I help you today?");
        rules.put("fertilizer", s -> "For nitrogen deficiency, use Urea. For potassium, use Muriate of Potash.");
        rules.put("disease", s -> "Please use the Disease Scanner module for accurate detection.");
        rules.put("weather", s -> "You can check live weather in the Weather Panel.");
    }

    public String getResponse(String input) {
        String lowerInput = input.toLowerCase();
        return rules.entrySet().stream()
                .filter(entry -> lowerInput.contains(entry.getKey()))
                .map(entry -> entry.getValue().apply(input))
                .findFirst()
                .orElse("I'm not sure about that. Try asking about fertilizers, diseases, or weather.");
    }
}