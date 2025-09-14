package com.whyvo.abm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.whyvo.abm.AttributeBaseModifier;
import com.whyvo.abm.rule.ModifyRule;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public class RuleCommand {
    private static final DynamicCommandExceptionType NOTHING_CHANGE_EXCEPTION = new DynamicCommandExceptionType(
            s -> Component.literal("Nothing change. The rule is already " + s)
    );
    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            id -> Component.literal("Rule not found: " + id)
    );
    private static final Function<Enable, Component> SUCCESS_MESSAGE = e -> Component.literal("The rule is now " + e);

    public static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = (context, builder) -> SharedSuggestionProvider.suggestResource(AttributeBaseModifier.RULE_MANAGER.getAllRules(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("abmrule").requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .suggests(SUGGESTION_PROVIDER)
                                .then(Commands.literal("enable")
                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), Enable.ENABLE))
                                )
                                .then(Commands.literal("disable")
                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), Enable.DISABLE))
                                )
                        )
        );
    }

    private static int execute(CommandSourceStack context, ResourceLocation name, Enable enable) throws CommandSyntaxException {
        ModifyRule rule = AttributeBaseModifier.RULE_MANAGER.get(name);
        if(rule == null) {
            throw NOT_FOUND_EXCEPTION.create(name);
        }
        if(rule.enable == enable.value) {
            throw NOTHING_CHANGE_EXCEPTION.create(enable);
        }
        rule.enable = enable.value;
        context.sendSystemMessage(SUCCESS_MESSAGE.apply(enable));
        return 1;
    }

    private enum Enable {
        ENABLE(true, "enable"),
        DISABLE(false, "disable");

        public final boolean value;
        public final String name;

        Enable(boolean value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
