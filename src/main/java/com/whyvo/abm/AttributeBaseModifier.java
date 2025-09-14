package com.whyvo.abm;

import com.whyvo.abm.command.RuleCommand;
import com.whyvo.abm.rule.RuleManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeBaseModifier implements ModInitializer {
	public static final String MOD_ID = "abm";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RuleManager RULE_MANAGER = new RuleManager();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private static final Identifier RELOAD_ID = Identifier.of(AttributeBaseModifier.MOD_ID, "reload");

			@Override
			public void reload(ResourceManager manager) {
				AttributeBaseModifier.RULE_MANAGER.load(manager);
			}

			@Override
			public Identifier getFabricId() {
				return RELOAD_ID;
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RuleCommand.register(dispatcher);
		});

		LOGGER.info("AttributeBaseModifier initialized!");
	}
}