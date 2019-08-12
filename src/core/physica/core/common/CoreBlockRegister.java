package physica.core.common;

import net.minecraft.util.EnumChatFormatting;
import physica.CoreReferences;
import physica.api.core.abstraction.AbstractionLayer;
import physica.api.core.conductor.EnumConductorType;
import physica.api.core.load.IContent;
import physica.api.core.load.LoadPhase;
import physica.core.common.block.BlockBatteryBox;
import physica.core.common.block.BlockBlastFurnace;
import physica.core.common.block.BlockCoalGenerator;
import physica.core.common.block.BlockElectricFurnace;
import physica.core.common.block.BlockEnergyCable;
import physica.core.common.block.BlockFulmination;
import physica.core.common.block.BlockInfiniteEnergy;
import physica.core.common.block.BlockLead;
import physica.core.common.block.BlockOre;
import physica.core.common.configuration.ConfigCore;
import physica.core.common.tile.TileBatteryBox;
import physica.core.common.tile.TileBlastFurnace;
import physica.core.common.tile.TileCoalGenerator;
import physica.core.common.tile.TileElectricFurnace;
import physica.core.common.tile.TileFulmination;
import physica.core.common.tile.TileInfiniteEnergy;
import physica.core.common.tile.cable.TileEnergyCable;
import physica.library.energy.ElectricityDisplay;
import physica.library.energy.ElectricityUtilities;
import physica.library.energy.base.Unit;
import physica.library.item.ItemBlockDescriptable;
import physica.library.item.ItemBlockMetadata;

public class CoreBlockRegister implements IContent {

	public static BlockInfiniteEnergy	blockInfEnergy;
	public static BlockFulmination		blockFulmination;
	public static BlockBlastFurnace		blockBlastFurnace;
	public static BlockCoalGenerator	blockCoalGenerator;
	public static BlockElectricFurnace	blockElectricFurnace;
	public static BlockBatteryBox		blockBatteryBox;
	public static BlockEnergyCable		blockCable;
	public static BlockLead				blockLead;
	public static BlockOre				blockTinOre;
	public static BlockOre				blockCopperOre;
	public static BlockOre				blockLeadOre;
	public static BlockOre				blockSilverOre;

	@Override
	public void register(LoadPhase phase)
	{
		if (phase == LoadPhase.RegisterObjects)
		{
			AbstractionLayer.Registering.registerBlock(blockInfEnergy = new BlockInfiniteEnergy(), ItemBlockDescriptable.class, "infEnergy");
			AbstractionLayer.Registering.registerTileEntity(TileInfiniteEnergy.class, CoreReferences.PREFIX + "infEnergy");
			AbstractionLayer.Registering.registerBlock(blockFulmination = new BlockFulmination(), ItemBlockDescriptable.class, "fulmination");
			AbstractionLayer.Registering.registerTileEntity(TileFulmination.class, CoreReferences.PREFIX + "fulmination");
			AbstractionLayer.Registering.registerBlock(blockBlastFurnace = new BlockBlastFurnace(), ItemBlockDescriptable.class, "blastFurnace");
			AbstractionLayer.Registering.registerTileEntity(TileBlastFurnace.class, CoreReferences.PREFIX + "blastFurnace");
			AbstractionLayer.Registering.registerBlock(blockCoalGenerator = new BlockCoalGenerator(), ItemBlockDescriptable.class, "coalGenerator");
			AbstractionLayer.Registering.registerTileEntity(TileCoalGenerator.class, CoreReferences.PREFIX + "coalGenerator");
			AbstractionLayer.Registering.registerBlock(blockBatteryBox = new BlockBatteryBox(), ItemBlockMetadata.class, "batteryBox");
			AbstractionLayer.Registering.registerTileEntity(TileBatteryBox.class, CoreReferences.PREFIX + "batteryBox");
			AbstractionLayer.Registering.registerBlock(blockElectricFurnace = new BlockElectricFurnace(), ItemBlockMetadata.class, "electricFurnace");
			AbstractionLayer.Registering.registerTileEntity(TileElectricFurnace.class, CoreReferences.PREFIX + "electricFurnace");
			AbstractionLayer.Registering.registerBlock(blockCable = new BlockEnergyCable(), ItemBlockMetadata.class, "energyCable");
			AbstractionLayer.Registering.registerTileEntity(TileEnergyCable.class, CoreReferences.PREFIX + "energyCable");
			AbstractionLayer.Registering.registerBlock(blockTinOre = new BlockOre("tinOre", ConfigCore.TIN_ORE_HARVEST_LEVEL), "tinOre");
			AbstractionLayer.Registering.registerBlock(blockCopperOre = new BlockOre("copperOre", ConfigCore.COPPER_ORE_HARVEST_LEVEL), "copperOre");
			AbstractionLayer.Registering.registerBlock(blockLeadOre = new BlockOre("leadOre", ConfigCore.LEAD_ORE_HARVEST_LEVEL), "leadOre");
			AbstractionLayer.Registering.registerBlock(blockSilverOre = new BlockOre("silverOre", ConfigCore.SILVER_ORE_HARVEST_LEVEL), "silverOre");
			AbstractionLayer.Registering.registerBlock(blockLead = new BlockLead(), "blockLead");
			AbstractionLayer.Registering.registerOre("oreLead", blockLeadOre);
			AbstractionLayer.Registering.registerOre("oreTin", blockTinOre);
			AbstractionLayer.Registering.registerOre("oreCopper", blockCopperOre);
			AbstractionLayer.Registering.registerOre("oreSilver", blockSilverOre);

			for (EnumConductorType en : EnumConductorType.values())
			{
				if (en == EnumConductorType.superConductor)
				{
					ItemBlockDescriptable.addDescription(blockCable, en.ordinal(),
							EnumChatFormatting.GOLD + "Max Power: " + EnumChatFormatting.GRAY + ElectricityDisplay.getDisplay(ElectricityUtilities.convertEnergy((double) en.getTransferRate(), Unit.RF, Unit.WATT), Unit.WATT),
							EnumChatFormatting.AQUA + "Max Voltage:" + EnumChatFormatting.GRAY + " infinite");
				} else
				{
					ItemBlockDescriptable.addDescription(blockCable, en.ordinal(),
							EnumChatFormatting.GOLD + "Max Power: " + EnumChatFormatting.GRAY + ElectricityDisplay.getDisplay(ElectricityUtilities.convertEnergy((double) en.getTransferRate(), Unit.RF, Unit.WATT), Unit.WATT),
							EnumChatFormatting.AQUA + "Max Voltage: " + EnumChatFormatting.GRAY + en.getVoltage() + " V");
				}
				ItemBlockDescriptable.addDescriptionShifted(blockCable, en.ordinal(), "Transfers energy from a source to", "different receivers in an energy", "network.");
			}
			ItemBlockDescriptable.addDescription(blockBatteryBox, 0,
					EnumChatFormatting.AQUA + "Capacity: " + EnumChatFormatting.GRAY
							+ ElectricityDisplay.getDisplayShort(ElectricityUtilities.convertEnergy((double) TileBatteryBox.BASE_ELECTRIC_CAPACITY, Unit.WATT, Unit.WATTHOUR), Unit.WATTHOUR),
					EnumChatFormatting.GOLD + "Power Transfer: " + EnumChatFormatting.GRAY + ElectricityDisplay.getDisplay(ElectricityUtilities.convertEnergy(TileBatteryBox.BASE_ELECTRIC_CAPACITY / 500, Unit.WATT, Unit.WATT), Unit.WATT));
			ItemBlockDescriptable.addDescription(blockBatteryBox, 1,
					EnumChatFormatting.AQUA + "Capacity: " + EnumChatFormatting.GRAY
							+ ElectricityDisplay.getDisplayShort(ElectricityUtilities.convertEnergy((double) TileBatteryBox.BASE_ELECTRIC_CAPACITY * 4, Unit.WATT, Unit.WATTHOUR), Unit.WATTHOUR),
					EnumChatFormatting.GOLD + "Power Transfer: " + EnumChatFormatting.GRAY
							+ ElectricityDisplay.getDisplay(ElectricityUtilities.convertEnergy(TileBatteryBox.BASE_ELECTRIC_CAPACITY * 4 / 500, Unit.WATT, Unit.WATT), Unit.WATT));
			ItemBlockDescriptable.addDescription(blockBatteryBox, 2,
					EnumChatFormatting.AQUA + "Capacity: " + EnumChatFormatting.GRAY
							+ ElectricityDisplay.getDisplayShort(ElectricityUtilities.convertEnergy((double) TileBatteryBox.BASE_ELECTRIC_CAPACITY * 12, Unit.WATT, Unit.WATTHOUR), Unit.WATTHOUR),
					EnumChatFormatting.GOLD + "Power Transfer: " + EnumChatFormatting.GRAY
							+ ElectricityDisplay.getDisplay(ElectricityUtilities.convertEnergy(TileBatteryBox.BASE_ELECTRIC_CAPACITY * 12 / 500, Unit.WATT, Unit.WATT), Unit.WATT));
			ItemBlockDescriptable.addDescriptionShifted(blockBatteryBox, 0, "A block that can be used to store electricity.");
			ItemBlockDescriptable.addDescriptionShifted(blockBatteryBox, 1, "A block that can be used to store electricity.");
			ItemBlockDescriptable.addDescriptionShifted(blockBatteryBox, 2, "A block that can be used to store electricity.");
			ItemBlockDescriptable.addDescriptionShifted(blockInfEnergy, 0, "Emits infinite energy into nearby receivers.");
			ItemBlockDescriptable.addDescriptionShifted(blockFulmination, 0, "Harvests energy from explosions.");
			ItemBlockDescriptable.addDescriptionShifted(blockBlastFurnace, 0, "Smelts iron and combines it with carbon", "to produce steel.");
			ItemBlockDescriptable.addDescriptionShifted(blockCoalGenerator, 0, "Generates electricity burning coal.");
			ItemBlockDescriptable.addDescriptionShifted(blockElectricFurnace, 0, "This block is a faster version of the furnace", "that runs on electricity.");
			ItemBlockDescriptable.addDescriptionShifted(blockElectricFurnace, 1, "This block is a faster version of both the furnace and", "the electric furnace that runs on electricity that also", "doubles the ore output when smelted.");

		}
	}
}