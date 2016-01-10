package wdl.litemod;

import java.io.File;
import java.util.List;

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
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
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
		return "1.7.10";
	}

	@Override
	public void init(File configPath) {

	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
		
	}
	
	public static void ingameMenuInit(EventInfo<GuiIngameMenu> eventinfo) {}
	
	public static void ingameMenuActionPerformed(EventInfo<GuiIngameMenu> eventinfo, GuiButton guibutton) {
		wdl.WDLHooks.handleWDLButtonClick(eventinfo.getSource(), guibutton);
		//more down here
	}
	
	public static void worldClientTick(EventInfo<WorldClient> eventinfo) {
		//more up here
		wdl.WDLHooks.onWorldClientTick(eventinfo.getSource());
	}
	
	public static void worldClientDoPreChunk(EventInfo<WorldClient> eventinfo, int p_73025_1_, int p_73025_2_, boolean p_73025_3_) {
		wdl.WDLHooks.onWorldClientDoPreChunk(eventinfo.getSource(), p_73025_1_, p_73025_2_, p_73025_3_);
        //more down here
	}
	
	public static void worldClientRemoveEntityFromWorld(ReturnEventInfo<WorldClient, Entity> eventinfo, int p_73028_1_) { //return entity
		wdl.WDLHooks.onWorldClientRemoveEntityFromWorld(eventinfo.getSource(), p_73028_1_);
        //more down here
	}
	
	public static void netHandlerPlayClientHandleDisconnect(EventInfo<NetHandlerPlayClient> eventinfo, S40PacketDisconnect p_147253_1_) {
        if (WDL.downloading)
        {
            WDL.stopDownload();

            try
            {
                Thread.sleep(2000);
            }
            catch (Exception var3)
            {

            }
        }
        //more down here
	}
	
	public static void netHandlerPlayClientOnDisconnect(EventInfo<NetHandlerPlayClient> eventinfo, IChatComponent p_147231_1_) {
        if (WDL.downloading)
        {
            WDL.stopDownload();

            try
            {
                Thread.sleep(2000);
            }
            catch (Exception var3)
            {
 
            }
        }
        //more down here
	}
	
	public static void netHandlerPlayClientHandleChat(EventInfo<NetHandlerPlayClient> eventinfo, S02PacketChat p_147251_1_) {
		//more up here
		wdl.WDLHooks.onNHPCHandleChat(eventinfo.getSource(), p_147251_1_);
	}
	
	public static void netHandlerPlayClientHandleBlockAction(EventInfo<NetHandlerPlayClient> eventinfo, S24PacketBlockAction packetIn) {
		//more up here
		wdl.WDLHooks.onNHPCHandleBlockAction(eventinfo.getSource(), packetIn);
	}
	
	public static void netHandlerPlayClientHandleMaps(EventInfo<NetHandlerPlayClient> eventinfo, S34PacketMaps packetIn) {
		//more up here
		wdl.WDLHooks.onNHPCHandleMaps(eventinfo.getSource(), packetIn);
	}
	
	public static void netHandlerPlayClientHandleCustomPayload(EventInfo<NetHandlerPlayClient> eventinfo, S3FPacketCustomPayload packetIn) {
		//more up here
		wdl.WDLHooks.onNHPCHandleCustomPayload(eventinfo.getSource(), packetIn);
	}
	

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	//handleBlockAction
	//handleMaps
	//handleCustomPayload
	@Override
	public void onRenderGui(GuiScreen currentScreen) {
		if ((currentScreen instanceof GuiIngameMenu) && !(currentScreen instanceof GuiScreenInject)) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenInject());
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
