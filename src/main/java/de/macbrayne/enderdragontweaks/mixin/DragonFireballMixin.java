package de.macbrayne.enderdragontweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends AbstractHurtingProjectile {
    protected DragonFireballMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void preventSelfHits(HitResult hitResult, CallbackInfo ci) {
        if(hitResult.getType() == HitResult.Type.ENTITY && this.ownedBy(((EntityHitResult)hitResult).getEntity())) {
            this.discard();
            ci.cancel();
        }
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private void increaseAreaEffectDuration(AreaEffectCloud instance, int i, Operation<Void> original) {
        original.call(instance, (int)(i * 1.5f));
    }
}
