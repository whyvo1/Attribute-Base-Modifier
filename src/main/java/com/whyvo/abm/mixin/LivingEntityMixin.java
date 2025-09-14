package com.whyvo.abm.mixin;

import com.whyvo.abm.AttributeBaseModifier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract AttributeContainer getAttributes();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getMaxHealth()F"))
    private void injectInit(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        if(!world.isClient) {
            AttributeContainer attributes = this.getAttributes();
            AttributeBaseModifier.RULE_MANAGER.applyRules(entityType, attributes);
        }
    }
}
