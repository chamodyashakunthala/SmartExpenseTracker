package com.example.smartexpensetracker.ai;

import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    public GeminiRequest(String promptText) {
        Part part = new Part(promptText);
        Content content = new Content(List.of(part));
        this.contents = List.of(content);
    }

    public static class Content {
        public List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}