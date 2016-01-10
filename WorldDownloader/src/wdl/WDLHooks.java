package wdl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemMap;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import wdl.api.IBlockEventListener;
import wdl.api.IChatMessageListener;
import wdl.api.IGuiHooksListener;
import wdl.api.IPluginChannelListener;
import wdl.gui.GuiWDL;
import wdl.gui.GuiWDLAbout;
import wdl.gui.GuiWDLPermissions;

/**
 * The various hooks for WDL. <br/>
 * All of these should be called regardless of any WDL state variables.
 */
public class WDLHooks {
	private static final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
	
	/**
	 * All WDLMods that implement {@link IGuiHooksListener}.
	 */
	public static Map<String, IGuiHooksListener> guiListeners =
			new HashMap<String, IGuiHooksListener>();
	/**
	 * All WDLMods that implement {@link IChatMessageListener}.
	 */
	public static Map<String, IChatMessageListener> chatMessageListeners =
			new HashMap<String, IChatMessageListener>();
	/**
	 * All WDLMods that implement {@link IPluginChannelListener}.
	 */
	public static Map<String, IPluginChannelListener> pluginChannelListeners =
			new HashMap<String, IPluginChannelListener>();
	/**
	 * All WDLMods that implement {@link IBlockEventListener}.
	 */
	public static Map<String, IBlockEventListener> blockEventListeners =
			new HashMap<String, IBlockEventListener>();
	
	/**
	 * Called when {@link WorldClient#tick()} is called.
	 * <br/>
	 * Should be at end of the method.
	 */
	public static void onWorldClientTick(WorldClient sender) {
		try {
			profiler.startSection("wdl");
			
			@SuppressWarnings("unchecked")
			List<EntityPlayer> players = ImmutableList.copyOf(sender.playerEntities);
			
			if (sender != WDL.worldClient) {
				profiler.startSection("onWorldLoad");
				if (WDL.worldLoadingDeferred) {
					return;
				}
				
				WDLEvents.onWorldLoad(sender);
				profiler.endSection();
			} else {
				profiler.startSection("inventoryCheck");
				if (WDL.downloading && WDL.thePlayer != null) {
					if (WDL.thePlayer.openContainer != WDL.windowContainer) {
						if (WDL.thePlayer.openContainer == WDL.thePlayer.inventoryContainer) {
							boolean handled;
							
							profiler.startSection("onItemGuiClosed");
							profiler.startSection("Core");
							handled = WDLEvents.onItemGuiClosed();
							profiler.endSection();
							
							Container container = WDL.thePlayer.openContainer;
							if (WDL.lastEntity != null) {
								Entity entity = WDL.lastEntity;

								for (Map.Entry<String, IGuiHooksListener> e :
										guiListeners.entrySet()) {
									if (handled) {
										break;
									}

									profiler.startSection(e.getKey());
									handled = e.getValue().onEntityGuiClosed(
											sender, entity, container);
									profiler.endSection();
								}
								
								if (!handled) {
									WDLMessages.chatMessageTranslated(
											WDLMessageTypes.ON_GUI_CLOSED_WARNING,
											"wdl.messages.onGuiClosedWarning.unhandledEntity",
											entity);
								}
							} else {
								BlockPos pos = WDL.lastClickedBlock;
								for (Map.Entry<String, IGuiHooksListener> e :
										guiListeners.entrySet()) {
									if (handled) {
										break;
									}

									profiler.startSection(e.getKey());
									handled = e.getValue().onBlockGuiClosed(
											sender, pos, container);
									profiler.endSection();
								}
								
								if (!handled) {
									WDLMessages.chatMessageTranslated(
											WDLMessageTypes.ON_GUI_CLOSED_WARNING,
											"wdl.messages.onGuiClosedWarning.unhandledTileEntity",
											pos, sender.getTileEntity(pos.getX(), pos.getY(), pos.getZ()));
								}
							}
							
							profiler.endSection();
						} else {
							profiler.startSection("onItemGuiOpened");
							profiler.startSection("Core");
							WDLEvents.onItemGuiOpened();
							profiler.endSection();
							profiler.endSection();
						}
	
						WDL.windowContainer = WDL.thePlayer.openContainer;
					}
				}
				profiler.endSection();
			}
			
			profiler.endStartSection("capes");
			CapeHandler.onWorldTick(players);
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e, "WDL mod: exception in onWorldClientTick event");
		}
	}

	/**
	 * Called when {@link WorldClient#doPreChunk(int, int, boolean)} is called.
	 * <br/>
	 * Should be at the start of the method.
	 */
	public static void onWorldClientDoPreChunk(WorldClient sender, int x,
			int z, boolean loading) {
		try {
			if (!WDL.downloading) { return; }
			
			profiler.startSection("wdl");
			
			if (!loading) {
				profiler.startSection("onChunkNoLongerNeeded");
				Chunk c = sender.getChunkFromChunkCoords(x, z); 
				
				profiler.startSection("Core");
				wdl.WDLEvents.onChunkNoLongerNeeded(c);
				profiler.endSection();
				
				profiler.endSection();
			}
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e, "WDL mod: exception in onWorldDoPreChunk event");
		}
	}

	/**
	 * Called when {@link WorldClient#removeEntityFromWorld(int)} is called.
	 * <br/>
	 * Should be at the start of the method.
	 * 
	 * @param eid
	 *            The entity's unique ID.
	 */
	public static void onWorldClientRemoveEntityFromWorld(WorldClient sender,
			int eid) {
		try {
			if (!WDL.downloading) { return; }
			
			profiler.startSection("wdl.onRemoveEntityFromWorld");
			
			Entity entity = sender.getEntityByID(eid);
			
			profiler.startSection("Core");
			WDLEvents.onRemoveEntityFromWorld(entity);
			profiler.endSection();
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e,
					"WDL mod: exception in onWorldRemoveEntityFromWorld event");
		}
	}

	/**
	 * Called when {@link NetHandlerPlayClient#handleChat(S02PacketChat)} is
	 * called.
	 * <br/>
	 * Should be at the end of the method.
	 */
	public static void onNHPCHandleChat(NetHandlerPlayClient sender,
			S02PacketChat packet) {
		try {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
				return;
			}
			
			if (!WDL.downloading) { return; }
			
			profiler.startSection("wdl.onChatMessage");
			
			//func_148915_c returns the IChatComponent.
			String chatMessage = packet.func_148915_c().getUnformattedText();
			
			profiler.startSection("Core");
			WDLEvents.onChatMessage(chatMessage);
			profiler.endSection();
			
			for (Map.Entry<String, IChatMessageListener> e : 
					chatMessageListeners.entrySet()) {
				profiler.startSection(e.getKey());
				e.getValue().onChat(WDL.worldClient, chatMessage);
				profiler.endSection();
			}
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e, "WDL mod: exception in onNHPCHandleChat event");
		}
	}

	/**
	 * Called when {@link NetHandlerPlayClient#handleMaps(S34PacketMaps)} is
	 * called.
	 * <br/>
	 * Should be at the end of the method.
	 */
	public static void onNHPCHandleMaps(NetHandlerPlayClient sender,
			S34PacketMaps packet) {
		try {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
				return;
			}
			
			if (!WDL.downloading) { return; }
			
			profiler.startSection("wdl.onMapDataLoaded");
			
			int id = packet.getMapId();
			MapData mapData = ItemMap.loadMapData(packet.getMapId(),
					WDL.worldClient);
			
			profiler.startSection("Core");
			WDLEvents.onMapDataLoaded(id, mapData);
			profiler.endSection();
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e, "WDL mod: exception in onNHPCHandleMaps event");
		}
	}

	/**
	 * Called when
	 * {@link NetHandlerPlayClient#handleCustomPayload(S3FPacketCustomPayload)}
	 * is called.
	 * <br/>
	 * Should be at the end of the method.
	 */
	public static void onNHPCHandleCustomPayload(NetHandlerPlayClient sender,
			S3FPacketCustomPayload packet) {
		try {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
				return;
			}
			
			String channel = packet.func_149169_c();
			byte[] payload = packet.func_149168_d();
			
			profiler.startSection("Core");
			WDLEvents.onPluginChannelPacket(channel, payload);
			profiler.endSection();
			
			for (Map.Entry<String, IPluginChannelListener> e : 
					pluginChannelListeners.entrySet()) {
				profiler.startSection(e.getKey());
				e.getValue().onPluginChannelPacket(WDL.worldClient, channel,
						payload);
				profiler.endSection();
			}
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e,
					"WDL mod: exception in onNHPCHandleCustomPayload event");
		}
	}

	/**
	 * Called when
	 * {@link NetHandlerPlayClient#handleBlockAction(S24PacketBlockAction)} is
	 * called.
	 * <br/>
	 * Should be at the end of the method.
	 */
	public static void onNHPCHandleBlockAction(NetHandlerPlayClient sender,
			S24PacketBlockAction packet) {
		try {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
				return;
			}
			
			if (!WDL.downloading) { return; }
			
			profiler.startSection("wdl.onBlockEvent");
			
			BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
			Block block = packet.getBlockType();
			int data1 = packet.getData1();
			int data2 = packet.getData2();
			
			profiler.startSection("Core");
			WDLEvents.onBlockEvent(pos, block, data1, data2);
			profiler.endSection();
			
			for (Map.Entry<String, IBlockEventListener> e : 
					blockEventListeners.entrySet()) {
				profiler.startSection(e.getKey());
				e.getValue().onBlockEvent(WDL.worldClient, pos, block, 
						data1, data2);
				profiler.endSection();
			}
			
			profiler.endSection();
		} catch (Throwable e) {
			WDL.crashed(e,
					"WDL mod: exception in onNHPCHandleBlockAction event");
		}
	}
	
	/**
	 * Injects WDL information into a crash report.
	 * 
	 * Called at the end of {@link CrashReport#populateEnvironment()}.
	 * @param report
	 */
	public static void onCrashReportPopulateEnvironment(CrashReport report) {
		report.makeCategory("World Downloader Mod").addCrashSectionCallable("Info",
			new Callable<String>() {
				public String call() {
					return WDL.getDebugInfo();
				}
			});
	}

	/**
	 * Start button ID. Ascii-encoded 'WDLs' (World Downloader Start).
	 * Chosen to be unique.
	 */
	private static final int WDLs = 0x57444C73;
	/**
	 * Options button ID. Ascii-encoded 'WDLo' (World Downloader Options).
	 * Chosen to be unique.
	 */
	private static final int WDLo = 0x57444C6F;
	
	/**
	 * Adds the "Download this world" button to the ingame pause GUI.
	 * 
	 * @param gui
	 * @param buttonList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void injectWDLButtons(GuiIngameMenu gui, List buttonList) {
		int insertAtYPos = 0;
	
		for (Object obj : buttonList) {
			GuiButton btn = (GuiButton) obj;
	
			if (btn.id == 5) { // Button "Achievements"
				insertAtYPos = btn.yPosition + 24;
				break;
			}
		}
	
		// Move other buttons down one slot (= 24 height units)
		for (Object obj : buttonList) {
			GuiButton btn = (GuiButton) obj;
	
			if (btn.yPosition >= insertAtYPos) {
				btn.yPosition += 24;
			}
		}
	
		// Insert wdl buttons.
		GuiButton wdlDownload = new GuiButton(WDLs, gui.width / 2 - 100,
				insertAtYPos, 170, 20, null);
		
		GuiButton wdlOptions = new GuiButton(WDLo, gui.width / 2 + 71,
				insertAtYPos, 28, 20,
				I18n.format("wdl.gui.ingameMenu.settings"));
		
		if (WDL.minecraft.isIntegratedServerRunning()) {
			wdlDownload.displayString = I18n
					.format("wdl.gui.ingameMenu.downloadStatus.singlePlayer");
			wdlDownload.enabled = false;
		} else if (!WDLPluginChannels.canDownloadAtAll()) {
			if (WDLPluginChannels.canRequestPermissions()) {
				// Allow requesting permissions.
				wdlDownload.displayString = I18n
						.format("wdl.gui.ingameMenu.downloadStatus.request");
			} else {
				// Out of date plugin :/
				wdlDownload.displayString = I18n
						.format("wdl.gui.ingameMenu.downloadStatus.disabled");
				wdlDownload.enabled = false;
			}
		} else if (WDL.saving) {
			wdlDownload.displayString = I18n
					.format("wdl.gui.ingameMenu.downloadStatus.saving");
			wdlDownload.enabled = false;
			wdlOptions.enabled = false;
		} else if (WDL.downloading) {
			wdlDownload.displayString = I18n
					.format("wdl.gui.ingameMenu.downloadStatus.stop");
		} else {
			wdlDownload.displayString = I18n
					.format("wdl.gui.ingameMenu.downloadStatus.start");
		}
		buttonList.add(wdlDownload);
		buttonList.add(wdlOptions);
	}

	/**
	 * Handle clicks in the ingame pause GUI.
	 * 
	 * @param gui
	 * @param button
	 */
	public static void handleWDLButtonClick(GuiIngameMenu gui, GuiButton button) {
		if (!button.enabled) {
			return;
		}
	
		if (button.id == WDLs) { // "Start/Stop Download"
			if (WDL.minecraft.isIntegratedServerRunning()) {
				return; // WDL not available if in singleplayer or LAN server mode
			}
			
			if (!WDLPluginChannels.canDownloadAtAll()) {
				// TODO: A bit more complex logic - if they can't download in
				// most terrain, but they DO have chunk overrides, do we want
				// to open a GUI?
				if (WDLPluginChannels.canRequestPermissions()) {
					WDL.minecraft.displayGuiScreen(new GuiWDLPermissions(gui));
				} else {
					button.enabled = false;
				}
				
				return;
			}
			if (WDL.downloading) {
				WDL.stopDownload();
			} else {
				WDL.startDownload();
			}
		} else if (button.id == WDLo) { // "..." (options)
			if (WDL.minecraft.isIntegratedServerRunning()) {
				WDL.minecraft.displayGuiScreen(new GuiWDLAbout(gui));
			} else {
				WDL.minecraft.displayGuiScreen(new GuiWDL(gui));
			}
		} else if (button.id == 1) { // "Disconnect"
			WDL.stopDownload();
		}
	}
}
