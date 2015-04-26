package wdl.litemod;

import com.mumfrey.liteloader.core.runtime.Obf;

public class WDLObfuscationTable extends Obf
{
    public static WDLObfuscationTable GuiIngameMenu = new WDLObfuscationTable("net.minecraft.client.gui.GuiIngameMenu", "bwy");
    public static WDLObfuscationTable GuiButton = new WDLObfuscationTable("net.minecraft.client.gui.GuiButton", "bug");
    public static WDLObfuscationTable initGui = new WDLObfuscationTable("func_73866_w_", "b", "initGui");
    public static WDLObfuscationTable actionPerformed = new WDLObfuscationTable("func_146284_a", "a", "actionPerformed");
    public static WDLObfuscationTable World = new WDLObfuscationTable("net.minecraft.world.World", "aqu");
    public static WDLObfuscationTable WorldClient = new WDLObfuscationTable("net.minecraft.client.multiplayer.WorldClient", "cen");
    public static WDLObfuscationTable Block = new WDLObfuscationTable("net.minecraft.block.Block", "atr");
    public static WDLObfuscationTable Entity = new WDLObfuscationTable("net.minecraft.entity.Entity", "wv");
    public static WDLObfuscationTable tick = new WDLObfuscationTable("func_72835_b", "c", "tick");
    public static WDLObfuscationTable doPreChunk = new WDLObfuscationTable("func_73025_a", "b", "doPreChunk");
    public static WDLObfuscationTable removeEntityFromWorld = new WDLObfuscationTable("func_73028_b", "d", "removeEntityFromWorld");
    public static WDLObfuscationTable addBlockEvent = new WDLObfuscationTable("func_175641_c", "c", "addBlockEvent");
    public static WDLObfuscationTable NetHandlerPlayClient = new WDLObfuscationTable("net.minecraft.client.network.NetHandlerPlayClient", "cee");
    public static WDLObfuscationTable S40PacketDisconnect = new WDLObfuscationTable("net.minecraft.network.play.server.S40PacketDisconnect", "jj");
    public static WDLObfuscationTable IChatComponent = new WDLObfuscationTable("net.minecraft.util.IChatComponent", "hp");
    public static WDLObfuscationTable handleDisconnect = new WDLObfuscationTable("func_147253_a", "a", "handleDisconnect");
    public static WDLObfuscationTable onDisconnect = new WDLObfuscationTable("func_147231_a", "a", "onDisconnect");

    protected WDLObfuscationTable(String name)
    {
        super(name, name, name);
    }

    protected WDLObfuscationTable(String seargeName, String obfName)
    {
        super(seargeName, obfName, seargeName);
    }

    protected WDLObfuscationTable(String seargeName, String obfName, String mcpName)
    {
        super(seargeName, obfName, mcpName);
    }
}
