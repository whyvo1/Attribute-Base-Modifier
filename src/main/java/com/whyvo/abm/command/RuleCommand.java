package com.whyvo.abm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.whyvo.abm.AttributeBaseModifier;
import com.whyvo.abm.rule.ModifyRule;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;


public class RuleCommand {
    private static final DynamicCommandExceptionType NOTHING_CHANGE_EXCEPTION = new DynamicCommandExceptionType(
            s -> Text.literal("Nothing change. The rule is already " + s)
    );
    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            id -> Text.literal("Rule not found: " + id)
    );
    private static final Function<Enable, Text> SUCCESS_MESSAGE = e -> Text.literal("The rule is now " + e);

    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(AttributeBaseModifier.RULE_MANAGER.getAllRules(), builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("abmrule").requires((source) -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("name", IdentifierArgumentType.identifier())
                                .suggests(SUGGESTION_PROVIDER)
                                .then(CommandManager.literal("enable")
                                        .executes(context -> execute(context.getSource(), IdentifierArgumentType.getIdentifier(context, "name"), Enable.ENABLE))
                                )
                                .then(CommandManager.literal("disable")
                                        .executes(context -> execute(context.getSource(), IdentifierArgumentType.getIdentifier(context, "name"), Enable.DISABLE))
                                )
                        )
        );
    }

    private static int execute(ServerCommandSource context, Identifier name, Enable enable) throws CommandSyntaxException {
        ModifyRule rule = AttributeBaseModifier.RULE_MANAGER.get(name);
        if(rule == null) {
            throw NOT_FOUND_EXCEPTION.create(name);
        }
        if(rule.enable == enable.value) {
            throw NOTHING_CHANGE_EXCEPTION.create(enable);
        }
        rule.enable = enable.value;
        context.sendMessage(SUCCESS_MESSAGE.apply(enable));
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
