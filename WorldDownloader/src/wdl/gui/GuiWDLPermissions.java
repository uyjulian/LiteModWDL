package wdl.gui;

import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;

/**
 * GUI that shows the current permissions for the user.
 */
public class GuiWDLPermissions extends GuiScreen {
	/**
	 * Margins for the top and the bottom of the list.
	 */
	private static final int TOP_MARGIN = 61, BOTTOM_MARGIN = 32;
	
	/**
	 * Reload permissions button
	 */
	private GuiButton reloadButton;
	
	/**
	 * Ticks (20ths of a second) until this UI needs to refresh.
	 * 
	 * If -1, don't refresh.
	 */
	private int refreshTicks = -1;
	
	/**
	 * Recalculates the {@link #globalEntries} list.
	 */
	private final GuiScreen parent;
	
	private TextList list;
	
	/**
	 * Creates a new GUI with the given parent.
	 * 
	 * @param parent
	 */
	public GuiWDLPermissions(GuiScreen parent) {
		this.parent = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		this.buttonList.clear();
		
		this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29,
				"Done"));
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 155, 39, 100, 20,
				"Current perms"));
		if (WDLPluginChannels.canRequestPermissions()) {
			this.buttonList.add(new GuiButton(201, this.width / 2 - 50, 39, 100, 20,
					"Request perms"));
			this.buttonList.add(new GuiButton(202, this.width / 2 + 55, 39, 100, 20,
					"Chunk Overrides"));
		}
		
		reloadButton = new GuiButton(1, (this.width / 2) + 5, 18, 150, 20,
				"Reload permissions");
		this.buttonList.add(reloadButton);
		
		this.list = new TextList(mc, width, height, TOP_MARGIN, BOTTOM_MARGIN);
		
		list.addLine("§c§lThis is a work in progress.");
		
		if (!WDLPluginChannels.hasPermissions()) {
			return;
		}
		
		list.addBlankLine();
		if (!WDLPluginChannels.canRequestPermissions()) {
			list.addLine("§cThe serverside permission plugin is out of date " +
					"and does support permission requests.  Please go ask a " +
					"server administrator to update the plugin.");
			list.addBlankLine();
		}
		
		if (WDLPluginChannels.getRequestMessage() != null) {
			list.addLine("Note from the server moderators: ");
			list.addLine(WDLPluginChannels.getRequestMessage());
			list.addBlankLine();
		}
		
		list.addLine("These are your current permissions:");
		// TODO: I'd like to return the description lines here, but can't yet.
		// Of course, I'd need to put in some better lines than before.
		// Maybe also skip unsent permissions?
		list.addLine("Can download: "
				+ WDLPluginChannels.canDownloadInGeneral());
		list.addLine("Can save chunks as you move: " + WDLPluginChannels.canCacheChunks());
		if (!WDLPluginChannels.canCacheChunks() && WDLPluginChannels.canDownloadInGeneral()) {
			list.addLine("Nearby chunk save radius: " + WDLPluginChannels.getSaveRadius());
		}
		list.addLine("Can save entities: "
				+ WDLPluginChannels.canSaveEntities());
		list.addLine("Can save tile entities: "
				+ WDLPluginChannels.canSaveTileEntities());
		list.addLine("Can save containers: "
				+ WDLPluginChannels.canSaveContainers());
		list.addLine("Received entity ranges: "
				+ WDLPluginChannels.hasServerEntityRange() + " ("
				+ WDLPluginChannels.getEntityRanges().size() + " total)");
	}
	
	@Override
	public void updateScreen() {
		if (refreshTicks > 0) {
			refreshTicks--;
		} else if (refreshTicks == 0) {
			initGui();
			refreshTicks = -1;
		}
	}
	
	@Override
	public void onGuiClosed() {
		WDL.saveProps();
	}
	
	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		this.list.handleMouseInput();
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		list.func_148179_a(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (list.func_148181_b(mouseX, mouseY, state)) {
			return;
		}
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			// Send the init packet.
			C17PacketCustomPayload initPacket;
			try {
				initPacket = new C17PacketCustomPayload("WDL|INIT",
						new PacketBuffer(Unpooled.copiedBuffer(WDL.VERSION
								.getBytes("UTF-8"))));
			} catch (UnsupportedEncodingException e) {
				WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR,
						"wdl.messages.generalError.noUTF8");

				initPacket = new C17PacketCustomPayload("WDL|INIT",
						new PacketBuffer(Unpooled.buffer()));
			}
			WDL.minecraft.getNetHandler().addToSendQueue(initPacket);
			
			button.enabled = false;
			button.displayString = "Refershing...";
			
			refreshTicks = 50; // 2.5 seconds
		}
		if (button.id == 100) {
			this.mc.displayGuiScreen(this.parent);
		}
		if (button.id == 200) {
			// Would open this GUI; do nothing.
		}
		if (button.id == 201) {
			this.mc.displayGuiScreen(new GuiWDLPermissionRequest(this.parent));
		}
		if (button.id == 202) {
			this.mc.displayGuiScreen(new GuiWDLChunkOverrides(this.parent));
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (this.list == null) {
			return;
		}
		
		this.list.drawScreen(mouseX, mouseY, partialTicks);
		
		this.drawCenteredString(this.fontRendererObj, "Permission info",
				this.width / 2, 8, 0xFFFFFF);
		
		if (!WDLPluginChannels.hasPermissions()) {
			this.drawCenteredString(this.fontRendererObj,
					"No permissions received; defaulting to everything enabled.",
					this.width / 2, (this.height - 32 - 23) / 2 + 23
							- fontRendererObj.FONT_HEIGHT / 2, 0xFFFFFF);
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
