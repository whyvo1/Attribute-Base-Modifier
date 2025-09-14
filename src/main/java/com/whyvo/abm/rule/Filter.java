package com.whyvo.abm.rule;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class Filter {
    private final List<EntityType<?>> entityTypes;
    private final List<TagKey<EntityType<?>>> tags;
    private final MobCategory spawnGroup;

    private Filter(List<EntityType<?>> entityType, List<TagKey<EntityType<?>>> tags, MobCategory spawnGroup) {
        this.entityTypes = entityType;
        this.tags = tags;
        this.spawnGroup = spawnGroup;
    }

    public boolean test(EntityType<?> entityType) {
        if(this.spawnGroup == null) {
            return this.entityTypes.contains(entityType) || this.testTags(entityType);
        }
        else {
            return entityType.getCategory() == this.spawnGroup;
        }
    }

    private boolean testTags(EntityType<?> entityType) {
        for(TagKey<EntityType<?>> tag : this.tags) {
            if(entityType.is(tag)) {
                return true;
            }
        }
        return false;
    }

    public static Filter parse(JsonObject json) {
        if(json.has("type")) {
            List<EntityType<?>> entityTypes = new ArrayList<>();
            List<TagKey<EntityType<?>>> tags = new ArrayList<>();
            JsonElement type = json.get("type");
            JsonArray array;

            if(type instanceof JsonPrimitive primitive) {
                if(!primitive.isString()) {
                    throw new JsonParseException("Expected a string");
                }
                array = new JsonArray();
                array.add(primitive.getAsString());
            }
            else if(type instanceof JsonArray) {
                array = (JsonArray) type;
            }
            else {
                throw new JsonParseException("Expected an array or a string");
            }

            for (JsonElement element : array) {
                if(element instanceof JsonPrimitive primitive && primitive.isString()) {
                    String s = primitive.getAsString();
                    if(s.startsWith("#")) {
                        ResourceLocation id = ResourceLocation.tryParse(s.substring(1));
                        tags.add(TagKey.create(Registries.ENTITY_TYPE, id));
                    }
                    else {
                        ResourceLocation id = ResourceLocation.tryParse(s);
                        entityTypes.add(BuiltInRegistries.ENTITY_TYPE.getValue(id));
                    }
                }
                else {
                    throw new JsonParseException("Expected a string");
                }
            }

            return new Filter(entityTypes, tags, null);
        }
        if(json.has("spawn_group")) {
            return new Filter(null, null, tryParseSpawnGroup(json.get("spawn_group").getAsString()));
        }
        throw new JsonParseException("Expected type or group");
    }

    private static MobCategory tryParseSpawnGroup(String s) {
        for(MobCategory spawnGroup : MobCategory.values()) {
            if(spawnGroup.getName().equals(s)) {
                return spawnGroup;
            }
        }
        throw new IllegalArgumentException("Invalid spawn group: " + s);
    }

}
