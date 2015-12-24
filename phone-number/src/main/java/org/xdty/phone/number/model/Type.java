package org.xdty.phone.number.model;

public enum Type {
    NORMAL("normal"),
    POI("poi"),
    REPORT("report");

    private String text;

    Type(String text) {
        this.text = text;
    }

    public static Type fromString(String text) {
        if (text != null) {
            for (Type t : Type.values()) {
                if (t.getText().equalsIgnoreCase(text)) {
                    return t;
                }
            }
        }
        return NORMAL;
    }

    public String getText() {
        return text;
    }
}
