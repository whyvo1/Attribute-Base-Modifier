package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AttributeModify {
    private final Holder<Attribute> attribute;
    private final Modifiers modifiers;

    private AttributeModify(Holder<Attribute> attribute, Modifiers modifiers) {
        this.attribute = attribute;
        this.modifiers = modifiers;
    }

    public void modify(AttributeMap attributes) {
        AttributeInstance instance = attributes.getInstance(this.attribute);
        if(instance != null) {
            double baseValue = instance.getBaseValue();
            instance.setBaseValue(this.modifiers.modify(baseValue));
        }
    }

    public static AttributeModify parse(JsonObject json) {
        String id = json.get("attribute").getAsString();
        Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.tryParse(id)).orElse(null);
        if(attribute == null) {
            throw new JsonParseException("Unknown attribute " + id);
        }
        Modifiers modifiers = Modifiers.parse(json.getAsJsonArray("modifiers"));
        return new AttributeModify(attribute, modifiers);
    }
}
