package com.whyvo.abm.mixin;

import com.whyvo.abm.AttributeBaseModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract AttributeMap getAttributes();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMaxHealth()F"))
    private void injectInit(EntityType<? extends LivingEntity> entityType, Level world, CallbackInfo ci) {
        if(!world.isClientSide) {
            AttributeMap attributes = this.getAttributes();
            AttributeBaseModifier.RULE_MANAGER.applyRules(entityType, attributes);
        }
    }
}
