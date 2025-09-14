package com.whyvo.abm.mixin;

import com.whyvo.abm.AttributeBaseModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract AttributeMap getAttributes();

    @Shadow public abstract double getAttributeValue(Attribute p_21134_);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMaxHealth()F"))
    private float redirectInit(LivingEntity instance, EntityType<? extends LivingEntity> entityType, Level world) {
        if(!world.isClientSide) {
            AttributeMap attributes = this.getAttributes();
            AttributeBaseModifier.RULE_MANAGER.applyRules(entityType, attributes);
        }
        return (float)this.getAttributeValue(Attributes.MAX_HEALTH);
    }
}
