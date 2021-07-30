package moriyashiine.bewitchment.mixin.curse;

import moriyashiine.bewitchment.api.component.CursesComponent;
import moriyashiine.bewitchment.common.entity.interfaces.InsanityTargetAccessor;
import moriyashiine.bewitchment.common.registry.BWCurses;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.AxeItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"))
	private StatusEffectInstance modifyStatusEffect(StatusEffectInstance effect) {
		if (!world.isClient && !effect.isAmbient() && effect.getEffectType().getType() == StatusEffectType.HARMFUL && CursesComponent.get((LivingEntity) (Object) this).hasCurse(BWCurses.COMPROMISED)) {
			return new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier() + 1, false, effect.shouldShowParticles(), effect.shouldShowIcon());
		}
		return effect;
	}
	
	@ModifyVariable(method = "applyArmorToDamage", at = @At("HEAD"))
	private float modifyDamage(float amount, DamageSource source) {
		if (!world.isClient) {
			Entity directSource = source.getSource();
			CursesComponent cursesComponent = CursesComponent.get((LivingEntity) (Object) this);
			if (cursesComponent.hasCurse(BWCurses.FORESTS_WRATH) && (source.isFire() || ((directSource instanceof LivingEntity && ((LivingEntity) directSource).getMainHandStack().getItem() instanceof AxeItem)))) {
				amount *= 2;
			}
			if (cursesComponent.hasCurse(BWCurses.SUSCEPTIBILITY) && source.isMagic()) {
				amount *= 2;
			}
		}
		return amount;
	}
	
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!world.isClient && this instanceof InsanityTargetAccessor && ((InsanityTargetAccessor) this).getInsanityTargetUUID().isPresent()) {
			callbackInfo.setReturnValue(false);
		}
	}
	
	@Inject(method = "canHaveStatusEffect", at = @At("RETURN"), cancellable = true)
	private void canHaveStatusEffect(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (callbackInfo.getReturnValue() && !world.isClient && !effect.isAmbient() && effect.getEffectType().getType() == StatusEffectType.BENEFICIAL && CursesComponent.get((LivingEntity) (Object) this).hasCurse(BWCurses.UNLUCKY) && random.nextBoolean()) {
			callbackInfo.setReturnValue(false);
		}
	}
}
