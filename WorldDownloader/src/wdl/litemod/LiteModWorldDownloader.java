package wdl.litemod;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
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
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.IChatComponent;
import wdl.ReflectionUtils;
import wdl.WDL;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.ChatListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.Permissible;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.permissions.PermissionsManager;
import com.mumfrey.liteloader.permissions.PermissionsManagerClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;

public class LiteModWorldDownloader implements JoinGameListener, Tickable, Permissible, ChatListener
{
    @SuppressWarnings("unused")
	private boolean canCacheChunks = true;

    @Override
	public String getName()
    {
        return "WorldDownloader";
    }

    @Override
	public String getVersion()
    {
        return "1.1.3";
    }

    @Override
	public void init(File configPath) {}

    @Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        if (WDL.mc.theWorld != WDL.wc)
        {
            if (WDL.mc.theWorld == null)
            {
                WDL.stop();
                WDL.wc = null;
            }
            else
            {
                WDL.onWorldLoad();
            }
        }
    }

    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket) {}

    @Override
	public String getPermissibleModName()
    {
        return this.getName().toLowerCase();
    }

    @Override
	public float getPermissibleModVersion()
    {
        return Float.parseFloat(this.getVersion().replace(".", ""));
    }

    @Override
	public void registerPermissions(PermissionsManagerClient permissionsManager)
    {
        permissionsManager.registerModPermission(this, "cachechunks");
    }

    @Override
	public void onPermissionsCleared(PermissionsManager manager)
    {
        this.canCacheChunks = true;
    }

    @Override
	public void onPermissionsChanged(PermissionsManager manager)
    {
        this.canCacheChunks = ((PermissionsManagerClient)manager).getModPermission(this, "cachechunks");
    }

    @Override
	public void onChat(IChatComponent chat, String message)
    {
        WDL.handleServerSeedMessage(message);
    }

    @SuppressWarnings("rawtypes")
	public static void initGui(EventInfo<GuiIngameMenu> e)
    {
    	GuiIngameMenu ingameMenu = e.getSource();
    	List buttonList = (List)ReflectionUtils.getPrivateFieldValueByType(ingameMenu, GuiScreen.class, List.class);
        WDL.injectWDLButtons(ingameMenu, buttonList);
    }

    public static void actionPerformed(EventInfo<GuiIngameMenu> e, GuiButton p_146284_1_)
    {
        WDL.handleWDLButtonClick((GuiIngameMenu)e.getSource(), p_146284_1_);
    }

    public static void tick(EventInfo<WorldClient> e)
    {
        if (WDL.downloading && WDL.tp.openContainer != WDL.windowContainer)
        {
            if (WDL.tp.openContainer == WDL.tp.inventoryContainer)
            {
                WDL.onItemGuiClosed();
            }
            else
            {
                WDL.onItemGuiOpened();
            }

            WDL.windowContainer = WDL.tp.openContainer;
        }
    }

    public static void doPreChunk(EventInfo<WorldClient> e, int p_73025_1_, int p_73025_2_, boolean p_73025_3_)
    {
        WorldClient wc = (WorldClient)e.getSource();

        if (p_73025_3_)
        {
            if (wc != WDL.wc)
            {
                WDL.onWorldLoad();
            }
        }
        else if (WDL.downloading)
        {
            WDL.onChunkNoLongerNeeded(wc.getChunkProvider().provideChunk(p_73025_1_, p_73025_2_));
        }
    }

    public static void removeEntityFromWorld(ReturnEventInfo < WorldClient, ? > e, int p_73028_1_)
    {
    	if(WDL.downloading)
        {
            Entity entity = e.getSource().getEntityByID(p_73028_1_);
            if(entity != null)
            {
                int threshold = 0;
                if ((entity instanceof EntityFishHook) ||
                    (entity instanceof EntityArrow) ||
                    (entity instanceof EntitySmallFireball) ||
                    (entity instanceof EntitySnowball) ||
                    (entity instanceof EntityEnderPearl) ||
                    (entity instanceof EntityEnderEye) ||
                    (entity instanceof EntityEgg) ||
                    (entity instanceof EntityPotion) ||
                    (entity instanceof EntityExpBottle) ||
                    (entity instanceof EntityItem) ||
                    (entity instanceof EntitySquid))
                {
                    threshold = 64;
                }
                else if ((entity instanceof EntityMinecart) ||
                         (entity instanceof EntityBoat) ||
                         (entity instanceof IAnimals))
                {
                    threshold = 80;
                }
                else if ((entity instanceof EntityDragon) ||
                         (entity instanceof EntityTNTPrimed) ||
                         (entity instanceof EntityFallingBlock) ||
                         (entity instanceof EntityPainting) ||
                         (entity instanceof EntityXPOrb))
                {
                    threshold = 160;
                }
                double distance = entity.getDistance(WDL.tp.posX, entity.posY, WDL.tp.posZ);
                if( distance > threshold)
                {
                    WDL.chatDebug("removeEntityFromWorld: Refusing to remove " + EntityList.getEntityString(entity) + " at distance " + distance);
                    e.setReturnValue(null);
                }
                WDL.chatDebug("removeEntityFromWorld: Removing " + EntityList.getEntityString(entity) + " at distance " + distance);
            }
        }
    }

    public static void addBlockEvent(EventInfo<WorldClient> e, int par1, int par2, int par3, Block par4, int par5, int par6)
    {
        if (WDL.downloading)
        {
            WDL.onBlockEvent(par1, par2, par3, par4, par5, par6);
        }
    }

    public static void handleDisconnect(EventInfo<NetHandlerPlayClient> e, S40PacketDisconnect arg1)
    {
        if (WDL.downloading)
        {
            WDL.stop();

            try
            {
                Thread.sleep(2000L);
            }
            catch (Exception var3)
            {
                ;
            }
        }
    }

    public static void onDisconnect(EventInfo<NetHandlerPlayClient> e, IChatComponent arg1)
    {
        if (WDL.downloading)
        {
            WDL.stop();

            try
            {
                Thread.sleep(2000L);
            }
            catch (Exception var3)
            {
                ;
            }
        }
    }

	@Override
	public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer)
	{
	}
}