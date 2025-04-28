package net.river.hostilesinpeaceful.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set; // Import Set

@Mixin(GoalSelector.class)
public interface GoalSelectorAccessor {
    @Accessor("goals") // Make sure this targets the field named "goals"
    Set<PrioritizedGoal> getGoals(); // Change return type to Set
}