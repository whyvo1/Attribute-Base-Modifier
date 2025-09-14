package com.whyvo.abm.rule;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ModifyRule {
    private final Filter includeFilter;
    private final Filter excludeFilter;
    private final List<AttributeModify> modifies;
    public boolean enable;

    private ModifyRule(Filter includeFilter, Filter excludeFilter, List<AttributeModify> modifies) {
        this(includeFilter, excludeFilter, modifies, true);
    }

    private ModifyRule(Filter includeFilter, Filter excludeFilter, List<AttributeModify> modifies, boolean enable) {
        this.includeFilter = includeFilter;
        this.excludeFilter = excludeFilter;
        this.modifies = modifies;
        this.enable = enable;
    }

    public boolean test(EntityType<?> entityType) {
        return (this.includeFilter == null || this.includeFilter.test(entityType)) &&
                (this.excludeFilter == null || !this.excludeFilter.test(entityType));
    }

    public void modify(AttributeMap attributes) {
        for (AttributeModify modify : this.modifies) {
            modify.modify(attributes);
        }
    }

    public static ModifyRule parse(JsonObject json) {
        Filter includeFilter = null;
        Filter excludeFilter = null;
        if(json.has("include")) {
            JsonObject jsonObject = json.getAsJsonObject("include");
            includeFilter = Filter.parse(jsonObject);
        }
        if(json.has("exclude")) {
            JsonObject jsonObject = json.getAsJsonObject("exclude");
            excludeFilter = Filter.parse(jsonObject);
        }
        List<AttributeModify> modifies = new ArrayList<>();
        JsonArray jsonArray = json.getAsJsonArray("modifies");
        for(JsonElement jsonElement : jsonArray) {
            JsonObject o = jsonElement.getAsJsonObject();
            modifies.add(AttributeModify.parse(o));
        }
        if(json.has("default_enable")) {
            return new ModifyRule(includeFilter, excludeFilter, modifies, json.get("default_enable").getAsBoolean());
        }
        return new ModifyRule(includeFilter, excludeFilter, modifies);
    }
}
