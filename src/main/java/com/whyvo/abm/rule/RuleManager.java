package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.whyvo.abm.AttributeBaseModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class RuleManager {
    private static final FileToIdConverter FINDER = FileToIdConverter.json("abm_rules");

    private final Map<ResourceLocation, ModifyRule> rulesMap = new HashMap<>();
    private final List<ModifyRule> rules = new ArrayList<>();

    public void applyRules(EntityType<? extends LivingEntity> entityType, AttributeMap attributes) {
        for(ModifyRule rule : this.rules) {
            if(rule.enable && rule.test(entityType)) {
                rule.modify(attributes);
            }
        }
    }

    @Nullable
    public ModifyRule get(ResourceLocation id) {
        return this.rulesMap.get(id);
    }

    public Iterable<ResourceLocation> getAllRules() {
        return this.rulesMap.keySet();
    }

    public void load(ResourceManager resourceManager) {
        this.rulesMap.clear();
        this.rules.clear();

        Map<ResourceLocation, Resource> resources = FINDER.listMatchingResources(resourceManager);
        for(Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation id = FINDER.fileToId(entry.getKey());
            try {
                JsonObject json = JsonParser.parseReader(entry.getValue().openAsReader()).getAsJsonObject();
                ModifyRule rule = ModifyRule.parse(json);
                this.rulesMap.put(id, rule);
                this.rules.add(rule);
            } catch (Exception e) {
                AttributeBaseModifier.LOGGER.warn("Failed to parse rule {}, ignored", id);
                AttributeBaseModifier.LOGGER.debug("Failed loading rule: ", e);
            }
        }

        AttributeBaseModifier.LOGGER.info("Loaded {} rules", this.rules.size());
    }
}
