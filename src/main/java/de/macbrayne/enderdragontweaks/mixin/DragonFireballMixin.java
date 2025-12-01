package de.macbrayne.enderdragontweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonFireball.class)
public class DragonFireballMixin {
    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private void increaseAreaEffectDuration(AreaEffectCloud instance, int i, Operation<Void> original) {
        original.call(instance, (int)(i * 1.5f));
    }
}
