package wdl.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wdl.WDL;
import wdl.WDLHooks;
import wdl.WDLPluginChannels;

/**
 * Tool to allow other mods to interact with WDL.
 */
public class WDLApi {
	private static Logger logger = LogManager.getLogger();
	
	/**
	 * Saved a TileEntity to the given position.
	 * 
	 * @param pos The position to save at.
	 * @param te The TileEntity to save.
	 */
	public static void saveTileEntity(BlockPos pos, TileEntity te) {
		if (!WDLPluginChannels.canSaveTileEntities()) {
			logger.warn("API attempted to call saveTileEntity when " +
					"saving TileEntities is not allowed!  Pos: " + pos +
					", te: " + te + ".  StackTrace: ");
			logStackTrace();
			
			return;
		}
		
		WDL.newTileEntities.put(pos, te);
	}
	
	/**
	 * Adds a mod to the list of the listened mods.
	 */
	public static void addWDLMod(IWDLMod mod) {
		if (mod == null) {
			throw new IllegalArgumentException("mod must not be null!");
		}
		
		String modName = mod.getName();
		if (WDLHooks.wdlMods.containsKey(modName)) {
			throw new IllegalArgumentException("A mod by the name of '"
					+ modName + "' is already registered by "
					+ WDLHooks.wdlMods.get(modName) + " (tried to register "
					+ mod + " over it)");
		}
		
		WDLHooks.wdlMods.put(modName, mod);
	}
	
	/**
	 * Writes out the current stacktrace to the logger in warn mode.
	 */
	private static void logStackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (StackTraceElement e : elements) {
			logger.warn(e.toString());
		}
	}
}
