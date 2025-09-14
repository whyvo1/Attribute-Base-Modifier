package com.whyvo.abm;

import com.mojang.logging.LogUtils;
import com.whyvo.abm.command.RuleCommand;
import com.whyvo.abm.rule.RuleManager;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.slf4j.Logger;

@Mod(AttributeBaseModifier.MOD_ID)
public class AttributeBaseModifier {

    public static final String MOD_ID = "abm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final RuleManager RULE_MANAGER = new RuleManager();

    public AttributeBaseModifier(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("AttributeBaseModifier initialized!");
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ResourceManagerReloadListener() {
            private static final String NAME = "abm:reload";

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                AttributeBaseModifier.RULE_MANAGER.load(manager);
            }

            @Override
            public String getName() {
                return NAME;
            }
        });
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        RuleCommand.register(event.getDispatcher());
    }
}
