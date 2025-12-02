package de.macbrayne.enderdragontweaks.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin extends Projectile {
    public AbstractHurtingProjectileMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
    private void preventEnderDragonSelfHits(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (((AbstractHurtingProjectile)(Object)this) instanceof DragonFireball fireball && entity instanceof EnderDragon) {
            if (this.ownedBy(entity)) {
                cir.setReturnValue(false);
            }
        }
    }
}
