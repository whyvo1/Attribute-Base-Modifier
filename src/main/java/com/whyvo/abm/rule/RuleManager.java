package com.whyvo.abm.rule;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.whyvo.abm.AttributeBaseModifier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleManager {
    private static final ResourceFinder FINDER = ResourceFinder.json("abm_rules");

    private final Map<Identifier, ModifyRule> rulesMap = new HashMap<>();
    private final List<ModifyRule> rules = new ArrayList<>();

    public void applyRules(EntityType<? extends LivingEntity> entityType, AttributeContainer attributes) {
        for(ModifyRule rule : this.rules) {
            if(rule.enable && rule.test(entityType)) {
                rule.modify(attributes);
            }
        }
    }

    @Nullable
    public ModifyRule get(Identifier id) {
        return this.rulesMap.get(id);
    }

    public Iterable<Identifier> getAllRules() {
        return this.rulesMap.keySet();
    }

    public void load(ResourceManager resourceManager) {
        this.rulesMap.clear();
        this.rules.clear();

        Map<Identifier, Resource> resources = FINDER.findResources(resourceManager);
        for(Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier id = FINDER.toResourceId(entry.getKey());
            try {
                JsonObject json = JsonParser.parseReader(entry.getValue().getReader()).getAsJsonObject();
                ModifyRule rule = ModifyRule.parse(json);
                this.rulesMap.put(id, rule);
                this.rules.add(rule);
            } catch (Exception e) {
                AttributeBaseModifier.LOGGER.warn("Failed to parse rule {}, ignored", id.toString());
                AttributeBaseModifier.LOGGER.debug("Failed loading rule: ", e);
            }
        }

        AttributeBaseModifier.LOGGER.info("Loaded {} rules", this.rules.size());
    }
}
