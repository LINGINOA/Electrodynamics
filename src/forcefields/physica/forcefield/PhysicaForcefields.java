package physica.forcefield;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import physica.CoreReferences;
import physica.api.core.abstraction.recipe.IRecipeRegister;
import physica.api.core.load.ContentLoader;
import physica.api.core.load.LoadPhase;
import physica.forcefield.client.ForcefieldClientRegister;
import physica.forcefield.common.ConstructorWorldData;
import physica.forcefield.common.ForcefieldBlockRegister;
import physica.forcefield.common.ForcefieldEventHandler;
import physica.forcefield.common.ForcefieldFluidRegister;
import physica.forcefield.common.ForcefieldItemRegister;
import physica.forcefield.common.ForcefieldRecipeRegister;
import physica.forcefield.common.ForcefieldTabRegister;
import physica.forcefield.common.command.SetIdentityCommand;
import physica.forcefield.common.configuration.ConfigForcefields;
import physica.forcefield.common.tile.TileFortronFieldConstructor;
import physica.proxy.CommonProxy;

@Mod(modid = ForcefieldReferences.DOMAIN, name = ForcefieldReferences.NAME, version = CoreReferences.VERSION, dependencies = "required-after:" + CoreReferences.DOMAIN)
public class PhysicaForcefields {

	@SidedProxy(clientSide = "physica.proxy.ClientProxy", serverSide = "physica.proxy.ServerProxy")
	public static CommonProxy			sidedProxy;
	public static ContentLoader			proxyLoader		= new ContentLoader();

	@Instance(ForcefieldReferences.NAME)
	public static PhysicaForcefields	INSTANCE;
	@Metadata(ForcefieldReferences.DOMAIN)
	public static ModMetadata			metadata;

	public static File					configFolder;
	public static ConfigForcefields		config;

	public static int					DEFAULT_COLOR	= Color.CYAN.brighter().brighter().getRGB();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		INSTANCE = this;
		configFolder = new File(event.getModConfigurationDirectory(), "/" + ForcefieldReferences.DOMAIN);
		proxyLoader.addContent(sidedProxy);
		proxyLoader.addContent(config = new ConfigForcefields());

		proxyLoader.addContent(new ForcefieldTabRegister());

		proxyLoader.addContent(new ForcefieldBlockRegister());
		proxyLoader.addContent(new ForcefieldItemRegister());

		proxyLoader.addContent(new ForcefieldFluidRegister());

		if (event.getSide() == Side.CLIENT)
		{
			proxyLoader.addContent(new ForcefieldClientRegister());
		}

		proxyLoader.addContent(new ForcefieldRecipeRegister());

		MinecraftForge.EVENT_BUS.register(ForcefieldEventHandler.INSTANCE);

		metadata.authorList = CoreReferences.Metadata.AUTHORS;
		metadata.autogenerated = false;
		metadata.credits = CoreReferences.Metadata.CREDITS;
		metadata.description = CoreReferences.Metadata.DESCRIPTION.replace("Physica", ForcefieldReferences.NAME);
		metadata.modId = ForcefieldReferences.DOMAIN;
		metadata.name = ForcefieldReferences.NAME;
		metadata.parent = CoreReferences.DOMAIN;
		metadata.updateUrl = CoreReferences.Metadata.UPDATE_URL;
		metadata.url = CoreReferences.Metadata.URL;
		metadata.version = CoreReferences.VERSION;
		proxyLoader.callRegister(LoadPhase.CreativeTabRegister);
		proxyLoader.callRegister(LoadPhase.ConfigRegister);
		proxyLoader.callRegister(LoadPhase.RegisterObjects);
		proxyLoader.callRegister(LoadPhase.PreInitialize);
		proxyLoader.callRegister(LoadPhase.ClientRegister);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxyLoader.callRegister(LoadPhase.Initialize);
		proxyLoader.callRegister(LoadPhase.EntityRegister);
		proxyLoader.callRegister(LoadPhase.FluidRegister);
		proxyLoader.callRegister(LoadPhase.WorldRegister);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxyLoader.callRegister(LoadPhase.PostInitialize);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		proxyLoader.callRegister(LoadPhase.OnStartup);
		IRecipeRegister.callRegister("Forcefields");
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new SetIdentityCommand());
		ConstructorWorldData.load(event.getServer().worldServerForDimension(0));
	}

	public static ArrayList<TileFortronFieldConstructor> getRelevantConstructors(World world, double x, double y, double z)
	{
		return ForcefieldEventHandler.INSTANCE.getRelevantConstructors(world, x, y, z);
	}

	public static boolean isInForcefields(ArrayList<TileFortronFieldConstructor> constructors, double x, double y, double z)
	{
		for (TileFortronFieldConstructor constructor : constructors)
		{
			if (constructor.isProtecting(x, y, z))
			{
				return true;
			}
		}
		return false;
	}
}
