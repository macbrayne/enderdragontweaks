package de.macbrayne.enderdragontweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.macbrayne.enderdragontweaks.EnderDragonTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob implements Enemy {
    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;setHealth(F)V", shift = At.Shift.BEFORE), method = "<init>")
    private void increaseMaxHealth(EntityType<? extends EnderDragon> entityType, Level level, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) {
            double modifier = serverLevel.getGameRules().getInt(EnderDragonTweaks.HEALTH) / 200d;
            getAttributes().getInstance(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("enderdragontweaks", "healthboost"), modifier,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }


    @WrapOperation(method = "checkCrystals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;setHealth(F)V"))
    private void increaseHealthRegen(EnderDragon instance, float originalHealth, Operation<Float> original) {
        Level level = instance.level();
        if (level instanceof ServerLevel serverLevel) {
            float modifier = serverLevel.getGameRules().getInt(EnderDragonTweaks.HEALTH) / 200f - 1f;
            original.call(instance, originalHealth + modifier);
            return;
        }
        original.call(instance, originalHealth);
    }

    @ModifyExpressionValue(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;getMaxHealth()F"))
    private float adjustMaxSittingDamagePercentage(float originalMaxHealth, ServerLevel serverLevel, EnderDragonPart enderDragonPart, DamageSource damageSource, float amount) {
        float modifier = serverLevel.getGameRules().getInt(EnderDragonTweaks.HEALTH) / 200f;
        return originalMaxHealth / modifier;
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "HEAD"), cancellable = true)
    private void preventBedDamage(ServerLevel serverLevel, EnderDragonPart enderDragonPart, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if(damageSource.is(DamageTypes.BAD_RESPAWN_POINT)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Fixes https://bugs.mojang.com/browse/MC/issues/MC-272431
     */
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 adjustYMotion(Vec3 instance, double d, double e, double f, Operation<Vec3> original) {
        return original.call(instance, d, e * 10, f);
    }
}
