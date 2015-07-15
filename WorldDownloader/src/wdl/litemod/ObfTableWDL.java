package wdl.litemod;

import com.mumfrey.liteloader.core.runtime.Obf;

public class ObfTableWDL extends Obf {
	public static ObfTableWDL NetHandlerPlayClient = new ObfTableWDL("net/minecraft/client/network/NetHandlerPlayClient","cee");
	public static ObfTableWDL WorldClient = new ObfTableWDL("net/minecraft/client/multiplayer/WorldClient","cen");
	public static ObfTableWDL GuiIngameMenu = new ObfTableWDL("net/minecraft/client/gui/GuiIngameMenu","bwy");
	
	public static ObfTableWDL GuiButton = new ObfTableWDL("net/minecraft/client/gui/GuiButton","bug");
	public static ObfTableWDL Entity = new ObfTableWDL("net/minecraft/entity/Entity","wv");
	public static ObfTableWDL Block = new ObfTableWDL("net/minecraft/block/Block","atr");
	public static ObfTableWDL S40PacketDisconnect = new ObfTableWDL("net/minecraft/network/play/server/S40PacketDisconnect","jj");
	public static ObfTableWDL IChatComponent = new ObfTableWDL("net/minecraft/util/IChatComponent","ho");
	public static ObfTableWDL S02PacketChat = new ObfTableWDL("net/minecraft/network/play/server/S02PacketChat","iz");
	public static ObfTableWDL BlockPos = new ObfTableWDL("net/minecraft/util/BlockPos","dt");
	public static ObfTableWDL S24PacketBlockAction = new ObfTableWDL("net/minecraft/network/play/server/S24PacketBlockAction","iv");
	public static ObfTableWDL S34PacketMaps = new ObfTableWDL("net/minecraft/network/play/server/S40PacketDisconnect","jx");
	public static ObfTableWDL S3FPacketCustomPayload = new ObfTableWDL("net/minecraft/network/play/server/S40PacketDisconnect","ji");
	
	public static ObfTableWDL GuiIngameMenu_initGui = new ObfTableWDL("func_73866_w_","b","initGui");
	public static ObfTableWDL GuiIngameMenu_actionPerformed = new ObfTableWDL("func_146284_a","a","actionPerformed");
	
	public static ObfTableWDL WorldClient_tick = new ObfTableWDL("func_72835_b","c","tick");
	public static ObfTableWDL WorldClient_doPreChunk = new ObfTableWDL("func_73025_a","b","doPreChunk");
	public static ObfTableWDL WorldClient_removeEntityFromWorld = new ObfTableWDL("func_73028_b","d","removeEntityFromWorld");
	//seems like this isn't used in caa017493d9b90f0e9dc48e0269f910ee25bade9
//	public static ObfTableWDL WorldClient_addBlockEvent = new ObfTableWDL("func_175641_c","c","addBlockEvent");
	
	public static ObfTableWDL NetHandlerPlayClient_handleDisconnect = new ObfTableWDL("func_147253_a","a","handleDisconnect");
	public static ObfTableWDL NetHandlerPlayClient_onDisconnect = new ObfTableWDL("func_147231_a","a","onDisconnect");
	public static ObfTableWDL NetHandlerPlayClient_handleChat = new ObfTableWDL("func_147251_a","a","handleChat");
	public static ObfTableWDL NetHandlerPlayClient_handleBlockAction = new ObfTableWDL("func_147261_a","a","handleBlockAction");
	public static ObfTableWDL NetHandlerPlayClient_handleMaps = new ObfTableWDL("func_147264_a","a","handleMaps");
	public static ObfTableWDL NetHandlerPlayClient_handleCustomPayload = new ObfTableWDL("func_147240_a","a","handleCustomPayload");

	protected ObfTableWDL(String seargeName, String obfName) {
		super(seargeName, obfName);
	}
	
	protected ObfTableWDL(String seargeName, String obfName, String mcpName) {
		super(seargeName, obfName, mcpName);
	}

}
