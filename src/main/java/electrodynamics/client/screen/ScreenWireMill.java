package electrodynamics.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.References;
import electrodynamics.api.formatting.ElectricUnit;
import electrodynamics.api.utilities.ElectricityChatFormatter;
import electrodynamics.client.screen.generic.GenericContainerScreenUpgradeable;
import electrodynamics.common.inventory.container.ContainerWireMill;
import electrodynamics.common.tile.generic.GenericTileProcessor;
import electrodynamics.common.tile.processor.TileWireMill;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenWireMill extends GenericContainerScreenUpgradeable<ContainerWireMill> implements IHasContainer<ContainerWireMill> {
	public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(References.ID + ":textures/gui/wiremill.png");

	public ScreenWireMill(ContainerWireMill container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	@Override
	public ResourceLocation getScreenBackground() {
		return SCREEN_BACKGROUND;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		field_230712_o_.func_238422_b_(matrixStack, new TranslationTextComponent("gui.wiremill.usage", ElectricityChatFormatter.getDisplayShort(TileWireMill.REQUIRED_JOULES_PER_TICK * 20, ElectricUnit.WATT)),
				(float) field_238744_r_ + 77, (float) field_238745_s_ - 11, 4210752);
		field_230712_o_.func_238422_b_(matrixStack,
				new TranslationTextComponent("gui.wiremill.voltage", ElectricityChatFormatter.getDisplayShort(GenericTileProcessor.DEFAULT_BASIC_MACHINE_VOLTAGE, ElectricUnit.VOLTAGE)), (float) field_238744_r_ + 77,
				field_238745_s_, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float delta, int mouseX, int mouseY) {
		func_238474_b_(stack, guiLeft + 79, guiTop + 34, 212, 14, container.getBurnLeftScaled() + 1, 16);
	}
}