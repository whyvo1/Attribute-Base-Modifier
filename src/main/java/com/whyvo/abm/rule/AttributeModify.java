package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.attribute.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributeModify {
    private final RegistryEntry<EntityAttribute> attribute;
    private final Modifiers modifiers;

    private AttributeModify(RegistryEntry<EntityAttribute> attribute, Modifiers modifiers) {
        this.attribute = attribute;
        this.modifiers = modifiers;
    }

    public void modify(AttributeContainer attributes) {
        EntityAttributeInstance instance = attributes.getCustomInstance(this.attribute);
        if(instance != null) {
            double baseValue = instance.getBaseValue();
            instance.setBaseValue(this.modifiers.modify(baseValue));
        }
    }

    public static AttributeModify parse(JsonObject json) {
        String id = json.get("attribute").getAsString();
        RegistryEntry<EntityAttribute> attribute = Registries.ATTRIBUTE.getEntry(Identifier.tryParse(id)).orElse(null);
        if(attribute == null) {
            throw new JsonParseException("Unknown attribute " + id);
        }
        Modifiers modifiers = Modifiers.parse(json.getAsJsonArray("modifiers"));
        return new AttributeModify(attribute, modifiers);
    }
}
