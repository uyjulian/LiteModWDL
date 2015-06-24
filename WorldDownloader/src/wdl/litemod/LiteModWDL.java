package wdl.litemod;

import java.io.File;
import java.util.List;

import wdl.GuiWDL;
import wdl.WDL;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

public class LiteModWDL implements LiteMod, RenderListener {

	@Override
	public String getName() {
		return "LiteModWDL";
	}

	@Override
	public String getVersion() {
		return "1.8";
	}

	@Override
	public void init(File configPath) {

	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
		
	}
	
	public static void ingameMenuInit(EventInfo<GuiIngameMenu> eventinfo) {}
	
	public static void ingameMenuActionPerformed(EventInfo<GuiIngameMenu> eventinfo, GuiButton guibutton) {
		/* WDL >>> */
		wdl.WDL.handleWDLButtonClick(eventinfo.getSource(), guibutton);

		/* <<< WDL */
		//more down here
	}
	
	public static void worldClientTick(EventInfo<WorldClient> eventinfo) {
		//more up here
		/* WDL >>> */
		if (wdl.WDL.downloading) {
			if (wdl.WDL.thePlayer.openContainer != wdl.WDL.windowContainer) {
				if (wdl.WDL.thePlayer.openContainer == wdl.WDL.thePlayer.inventoryContainer) {
					wdl.WDLEvents.onItemGuiClosed();
				} else {
					wdl.WDLEvents.onItemGuiOpened();
				}

				wdl.WDL.windowContainer = wdl.WDL.thePlayer.openContainer;
			}
		}

		/* <<< WDL */
	}
	
	public static void worldClientDoPreChunk(EventInfo<WorldClient> eventinfo, int p_73025_1_, int p_73025_2_, boolean p_73025_3_) {
        if (p_73025_3_)
        {
			/* WDL >>> */
			if (eventinfo.getSource() != wdl.WDL.worldClient) {
				wdl.WDLEvents.onWorldLoad();
				/* <<< WDL */
			}
            
            //line here
            
        }
        else
        {
			/* WDL >>> */
			if (wdl.WDL.downloading) {
				wdl.WDLEvents.onChunkNoLongerNeeded(eventinfo.getSource().getChunkProvider().provideChunk(
						p_73025_1_, p_73025_2_));
				/* <<< WDL */
			}
            
            //line here
        }
        //more down here
	}
	
	public static void worldClientRemoveEntityFromWorld(ReturnEventInfo<WorldClient, Entity> eventinfo, int p_73028_1_) { //return entity
		/* WDL >>> */
		wdl.WDLEvents.onRemoveEntityFromWorld(eventinfo.getSource().getEntityByID(p_73028_1_));
		/* <<< WDL */
        //more down here
	}
	
	public static void worldClientAddBlockEvent(EventInfo<WorldClient> eventinfo, BlockPos pos, Block block, int eventId,
			int eventParam) {
		//more up here
		if (wdl.WDL.downloading) {
			wdl.WDLEvents.onBlockEvent(pos, block, eventId, eventParam);
		}
	}
	
	public static void netHandlerPlayClientHandleDisconnect(EventInfo<NetHandlerPlayClient> eventinfo, S40PacketDisconnect p_147253_1_) {
		/* WDL >>> */
		if (wdl.WDL.downloading) {
			wdl.WDL.stop();

			try {
				Thread.sleep(2000L);
			} catch (Exception var3) {
				;
			}
		}

		/* <<< WDL */
        //more down here
	}
	
	public static void netHandlerPlayClientOnDisconnect(EventInfo<NetHandlerPlayClient> eventinfo, IChatComponent p_147231_1_) {
		/* WDL >>> */
		if (wdl.WDL.downloading) {
			wdl.WDL.stop();

			try {
				Thread.sleep(2000L);
			} catch (Exception var3) {
				;
			}
		}

		/* <<< WDL */
        //more down here
	}
	
	public static void netHandlerPlayClientHandleChat(EventInfo<NetHandlerPlayClient> eventinfo, S02PacketChat p_147251_1_) {
		/* WDL >>> */
		String var2 = p_147251_1_.func_148915_c().getFormattedText();
		wdl.WDLEvents.onChatMessage(var2);
		/* <<< WDL */
        //more down here
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRenderGui(GuiScreen currentScreen) {
		if ((currentScreen instanceof GuiIngameMenu) && !(currentScreen instanceof GuiScreenInject)) {
			WDL.minecraft.displayGuiScreen(new GuiScreenInject());
		}
		
	}

	@Override
	public void onRenderWorld() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetupCameraTransform() {
		// TODO Auto-generated method stub
		
	}

}
