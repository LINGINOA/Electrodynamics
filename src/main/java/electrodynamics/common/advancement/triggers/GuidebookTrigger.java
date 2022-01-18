package electrodynamics.common.advancement.triggers;

import com.google.gson.JsonObject;

import electrodynamics.api.References;
import electrodynamics.common.settings.Constants;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GuidebookTrigger extends SimpleCriterionTrigger<GuidebookTrigger.TriggerInstance> {

	public static final ResourceLocation ID = new ResourceLocation(References.ID, "guidebook");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public GuidebookTrigger.TriggerInstance createInstance(JsonObject p_70644_, EntityPredicate.Composite instance, DeserializationContext p_70646_) {
		return new GuidebookTrigger.TriggerInstance(instance);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, instance -> Constants.DISPENSE_GUIDEBOOK);
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		public TriggerInstance(EntityPredicate.Composite composite) {
			super(GuidebookTrigger.ID, composite);
		}
	}

}
