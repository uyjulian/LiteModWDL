package wdl.litemod;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.BeforeReturn;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

public class WDLTransformer extends EventInjectionTransformer
{
    @Override
	protected void addEvents()
    {
        Event initGuiEvent = Event.getOrCreate("initIngameMenu");
        MethodInfo initGuiMethod = new MethodInfo(WDLObfuscationTable.GuiIngameMenu, WDLObfuscationTable.initGui, Void.TYPE, new Object[0]);
        BeforeReturn beforeReturn = new BeforeReturn();
        this.addEvent(initGuiEvent, initGuiMethod, beforeReturn);
        initGuiEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "initGui"));
        Event actionPerformedEvent = Event.getOrCreate("ingameMenuActionPerformed");
        MethodInfo actionPerformedMethod = new MethodInfo(WDLObfuscationTable.GuiIngameMenu, WDLObfuscationTable.actionPerformed, Void.TYPE, new Object[] {WDLObfuscationTable.GuiButton});
        MethodHead methodHead = new MethodHead();
        this.addEvent(actionPerformedEvent, actionPerformedMethod, methodHead);
        actionPerformedEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "actionPerformed"));
        Event tickEvent = Event.getOrCreate("worldClientTick");
        MethodInfo tickMethod = new MethodInfo(WDLObfuscationTable.WorldClient, WDLObfuscationTable.tick, Void.TYPE, new Object[0]);
        this.addEvent(tickEvent, tickMethod, beforeReturn);
        tickEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "tick"));
        Event doPreChunkEvent = Event.getOrCreate("preChunkEvent");
        MethodInfo doPreChunkMethod = new MethodInfo(WDLObfuscationTable.WorldClient, WDLObfuscationTable.doPreChunk, Void.TYPE, new Object[] {Integer.TYPE, Integer.TYPE, Boolean.TYPE});
        this.addEvent(doPreChunkEvent, doPreChunkMethod, methodHead);
        doPreChunkEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "doPreChunk"));
        Event removeEntityFromWorldEvent = Event.getOrCreate("removingEntity", true);
        MethodInfo removeEntityFromWorldMethod = new MethodInfo(WDLObfuscationTable.WorldClient, WDLObfuscationTable.removeEntityFromWorld, WDLObfuscationTable.Entity, new Object[] {Integer.TYPE});
        this.addEvent(removeEntityFromWorldEvent, removeEntityFromWorldMethod, methodHead);
        removeEntityFromWorldEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "removeEntityFromWorld"));
        Event addBlockEvent = Event.getOrCreate("addingBlock");
        MethodInfo addBlockMethod = new MethodInfo(WDLObfuscationTable.World, WDLObfuscationTable.addBlockEvent, Void.TYPE, new Object[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, WDLObfuscationTable.Block, Integer.TYPE, Integer.TYPE});
        this.addEvent(addBlockEvent, addBlockMethod, beforeReturn);
        addBlockEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "addBlockEvent"));
        Event handleDisconnectEvent = Event.getOrCreate("handlingDisconnecting");
        MethodInfo handleDisconnectMethod = new MethodInfo(WDLObfuscationTable.NetHandlerPlayClient, WDLObfuscationTable.handleDisconnect, Void.TYPE, new Object[] {WDLObfuscationTable.S40PacketDisconnect});
        this.addEvent(handleDisconnectEvent, handleDisconnectMethod, methodHead);
        handleDisconnectEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "handleDisconnect"));
        Event onDisconnectEvent = Event.getOrCreate("onDisconnecting");
        MethodInfo onDisconnectMethod = new MethodInfo(WDLObfuscationTable.NetHandlerPlayClient, WDLObfuscationTable.onDisconnect, Void.TYPE, new Object[] {WDLObfuscationTable.IChatComponent});
        this.addEvent(onDisconnectEvent, onDisconnectMethod, methodHead);
        onDisconnectEvent.addListener(new MethodInfo("wdl.litemod.LiteModWorldDownloader", "onDisconnect"));
    }
}
