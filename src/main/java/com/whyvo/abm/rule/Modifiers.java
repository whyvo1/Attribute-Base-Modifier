package com.whyvo.abm.rule;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

public class Modifiers {
    private final List<Modifier> modifiers;

    private Modifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public double modify(double base) {
        for(Modifier modifier : modifiers) {
            base = modifier.modify(base);
        }
        return base;
    }

    public static Modifiers parse(JsonArray array) {
        List<Modifier> modifiers = new ArrayList<>();
        for(JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            modifiers.add(parseModifier(obj));
        }
        return new Modifiers(modifiers);
    }

    private static Modifier parseModifier(JsonObject json) {
        double value = json.get("value").getAsDouble();
        String type = json.get("type").getAsString();
        if("set".equals(type)) {
            return createSet(value);
        }
        if("add".equals(type)) {
            return createAdd(value);
        }
        if("multiply".equals(type)) {
            return createMultiply(value);
        }
        throw new JsonParseException("Unknown modifier type: " + type);
    }

    private static Modifier createSet(double value) {
        return new Modifier(((base, value1) -> value1), value);
    }

    private static Modifier createAdd(double value) {
        return new Modifier(Double::sum, value);
    }

    private static Modifier createMultiply(double value) {
        return new Modifier((base, value1) -> base * value1, value);
    }

    private record Modifier(Operation operation, double value) {

        public double modify(double base) {
            return operation.modify(base, this.value);
        }
    }

    private interface Operation {
        double modify(double base, double value);
    }

}
