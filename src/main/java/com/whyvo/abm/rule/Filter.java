package com.whyvo.abm.rule;

import com.google.gson.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    private final List<EntityType<?>> entityTypes;
    private final List<TagKey<EntityType<?>>> tags;
    private final SpawnGroup spawnGroup;

    private Filter(List<EntityType<?>> entityType, List<TagKey<EntityType<?>>> tags, SpawnGroup spawnGroup) {
        this.entityTypes = entityType;
        this.tags = tags;
        this.spawnGroup = spawnGroup;
    }

    public boolean test(EntityType<?> entityType) {
        if(this.spawnGroup == null) {
            return this.entityTypes.contains(entityType) || this.testTags(entityType);
        }
        else {
            return entityType.getSpawnGroup() == this.spawnGroup;
        }
    }

    private boolean testTags(EntityType<?> entityType) {
        for(TagKey<EntityType<?>> tag : this.tags) {
            if(entityType.isIn(tag)) {
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
                        Identifier id = Identifier.tryParse(s.substring(1));
                        tags.add(TagKey.of(RegistryKeys.ENTITY_TYPE, id));
                    }
                    else {
                        Identifier id = Identifier.tryParse(s);
                        entityTypes.add(Registries.ENTITY_TYPE.get(id));
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

    private static SpawnGroup tryParseSpawnGroup(String s) {
        for(SpawnGroup spawnGroup : SpawnGroup.values()) {
            if(spawnGroup.getName().equals(s)) {
                return spawnGroup;
            }
        }
        throw new IllegalArgumentException("Invalid spawn group: " + s);
    }

}
