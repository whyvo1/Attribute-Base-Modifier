package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.attribute.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private static RegistryEntry<EntityAttribute> getAttributeEntry(Identifier id) {
        RegistryKey<EntityAttribute> key = RegistryKey.of(Registries.ATTRIBUTE.getKey(), id);
        return Registries.ATTRIBUTE.getEntry(key).orElse(null);
    }

    public static AttributeModify parse(JsonObject json) {
        String id = json.get("attribute").getAsString();
        RegistryEntry<EntityAttribute> attribute = getAttributeEntry(Identifier.tryParse(id));
        if(attribute == null) {
            throw new JsonParseException("Unknown attribute " + id);
        }
        Modifiers modifiers = Modifiers.parse(json.getAsJsonArray("modifiers"));
        return new AttributeModify(attribute, modifiers);
    }
}
