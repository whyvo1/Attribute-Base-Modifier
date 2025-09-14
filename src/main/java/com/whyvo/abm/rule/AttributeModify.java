package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private static Holder<Attribute> getAttributeEntry(ResourceLocation id) {
        ResourceKey<Attribute> key = ResourceKey.create(BuiltInRegistries.ATTRIBUTE.key(), id);
        return BuiltInRegistries.ATTRIBUTE.getHolder(key).orElse(null);
    }

    public static AttributeModify parse(JsonObject json) {
        String id = json.get("attribute").getAsString();
        Holder<Attribute> attribute = getAttributeEntry(ResourceLocation.tryParse(id));
        if(attribute == null) {
            throw new JsonParseException("Unknown attribute " + id);
        }
        Modifiers modifiers = Modifiers.parse(json.getAsJsonArray("modifiers"));
        return new AttributeModify(attribute, modifiers);
    }
}
