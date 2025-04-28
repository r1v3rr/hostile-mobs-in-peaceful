package net.river.hostilesinpeaceful.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow; // We NEED Shadow for goalSelector/targetSelector fields
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// Import logger
import net.river.hostilesinpeaceful.HostilesInPeaceful;
// Import Accessors needed for goal removal
import net.river.hostilesinpeaceful.mixin.GoalSelectorAccessor;
import net.river.hostilesinpeaceful.mixin.ActiveTargetGoalAccessor;

import java.util.Set; // Import Set


@Mixin(MobEntity.class)
// The class MobEntity itself is abstract, so our mixin should be too
public abstract class MobEntityMixin {

    // Inside MobEntityMixin class...

    @Shadow
    @Final
    protected GoalSelector goalSelector;
    @Shadow
    @Final
    protected GoalSelector targetSelector;

// ... (keep the isDisallowedInPeaceful inject) ...

    @Inject(method = "initGoals()V", at = @At("RETURN"))
    private void hostilesinpeaceful_removePeacefulAttackGoals(CallbackInfo ci) {
        World world = ((Entity) (Object) this).getWorld();

        if (world != null && world.getDifficulty() == Difficulty.PEACEFUL && (Object) this instanceof HostileEntity) {
            HostilesInPeaceful.LOGGER.info("MobEntityMixin.initGoals: Removing attack goals for {} in peaceful.", ((Entity) (Object) this).getDisplayName().getString());

            try {
                if (this.targetSelector != null) {
                    // Cast the shadowed selector to the accessor interface
                    Set<PrioritizedGoal> goals = ((GoalSelectorAccessor) this.targetSelector).getGoals();
                    goals.removeIf(prioritizedGoal -> {
                        if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> activeTargetGoal) {
                            // Cast the goal to the accessor interface
                            Class<?> targetClass = ((ActiveTargetGoalAccessor) activeTargetGoal).getTargetClass();
                            return PlayerEntity.class.isAssignableFrom(targetClass);
                        }
                        return false;
                    });
                }

                // Optional: remove other attack goals from goalSelector if needed
                // if (this.goalSelector != null) { ... }

            } catch (Exception e) {
                HostilesInPeaceful.LOGGER.error("Failed to modify goals during initGoals for entity {}: {}", ((Entity) (Object) this).getDisplayName().getString(), e.getMessage(), e);
            }
        }
    }
}