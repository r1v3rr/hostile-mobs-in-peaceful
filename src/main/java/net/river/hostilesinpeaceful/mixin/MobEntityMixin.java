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

    // Shadow the fields we need direct access to modify goals
    @Shadow @Final protected GoalSelector goalSelector;
    @Shadow @Final protected GoalSelector targetSelector;

    // Keep this mixin from Step 6 to prevent one type of despawning
    @Inject(method = "isDisallowedInPeaceful()Z", at = @At("HEAD"), cancellable = true)
    private void hostilesinpeaceful_allowInPeaceful(CallbackInfoReturnable<Boolean> cir) {
        World world = ((Entity)(Object)this).getWorld();
        if (world != null && world.getDifficulty() == Difficulty.PEACEFUL) {
            // Don't log every time, it's spammy. We know it works if mobs don't despawn *instantly*.
            // HostilesInPeaceful.LOGGER.info("MobEntityMixin: Forcing isDisallowedInPeaceful() to return false.");
            cir.setReturnValue(false);
        }
    }

    // --- NEW: Inject into initGoals to remove attack behavior ---
    @Inject(method = "initGoals()V", at = @At("RETURN")) // Inject after vanilla adds its goals
    private void hostilesinpeaceful_removePeacefulAttackGoals(CallbackInfo ci) {
        // Get the world instance using the inherited method
        World world = ((Entity)(Object)this).getWorld();

        // Only modify goals if in peaceful and the mob is hostile
        if (world != null && world.getDifficulty() == Difficulty.PEACEFUL && (Object)this instanceof HostileEntity) {
            HostilesInPeaceful.LOGGER.info("MobEntityMixin.initGoals: Removing attack goals for {} in peaceful.", ((Entity)(Object)this).getDisplayName().getString());

            // --- Reuse the goal removal logic ---
            try {
                // Use the shadowed targetSelector field
                if (this.targetSelector != null) {
                    Set<PrioritizedGoal> goals = ((GoalSelectorAccessor) this.targetSelector).getGoals();
                    goals.removeIf(prioritizedGoal -> {
                        if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> activeTargetGoal) {
                            Class<?> targetClass = ((ActiveTargetGoalAccessor) activeTargetGoal).getTargetClass();
                            // Remove goals targeting players
                            return PlayerEntity.class.isAssignableFrom(targetClass);
                        }
                        return false;
                    });
                }

                // Also potentially remove melee/ranged attack goals from the main goalSelector
                if (this.goalSelector != null) {
                    Set<PrioritizedGoal> goals = ((GoalSelectorAccessor) this.goalSelector).getGoals();
                    goals.removeIf(prioritizedGoal -> {
                        // Add checks for other common attack goals if needed
                        // For now, let's focus on the targeting part above.
                        // Example: return prioritizedGoal.getGoal() instanceof MeleeAttackGoal;
                        return false; // Placeholder
                    });
                }

            } catch (Exception e) {
                HostilesInPeaceful.LOGGER.error("Failed to modify goals during initGoals for entity {}: {}", ((Entity)(Object)this).getDisplayName().getString(), e.getMessage(), e);
            }
            // --- End of goal removal logic ---
        }
    }
    // We still need the abstract class because MobEntity is abstract
    // No need for the explicit extends LivingEntityMixin unless you actually have one
}