package wdl.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import wdl.api.IWDLMod;
import wdl.api.IWDLModDescripted;
import wdl.api.IWDLModWithGui;
import wdl.api.WDLApi;

/**
 * GUI showing the currently enabled mods, and their information.
 * 
 * It's composed of two halves, one that lists enabled extensions that can
 * be clicked, and the other that shows the details on the selected extension.
 * The two halves can be dragged up and down (which is why the logic is so
 * complex here; {@link GuiListExtended} was not designed for that).
 * 
 * @author Pokechu22
 */
public class GuiWDLExtensions extends GuiScreen {
	/**
	 * Top of the bottom list.
	 */
	private int bottomLocation;
	
	/**
	 * Height of the bottom area.
	 */
	private static final int TOP_HEIGHT = 23;
	/**
	 * Height of the middle section.
	 * 
	 * Equal to <code>{@link FontRenderer#FONT_HEIGHT} + 10</code>.
	 */
	private static final int MIDDLE_HEIGHT = 19;
	/**
	 * Height of the top area.
	 */
	private static final int BOTTOM_HEIGHT = 32;
	
	/**
	 * The currently selected mod.
	 */
	private int selectedModIndex = -1;
	
	private class ModList extends GuiListExtended {
		public ModList() {
			super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width,
					bottomLocation, TOP_HEIGHT, bottomLocation, 22);
			this.setShowSelectionBox(true);
		}
		
		private class ModEntry implements IGuiListEntry {
			public final IWDLMod mod;
			private final String modDesc;
			private GuiButton button;
			
			public ModEntry(IWDLMod mod) {
				this.mod = mod;
				String name = mod.getName();
				if (mod instanceof IWDLModDescripted) {
					String displayName = ((IWDLModDescripted) mod)
							.getDisplayName();
					
					if (displayName != null && !displayName.isEmpty()) {
						name = displayName;
					}
				}
				this.modDesc = I18n.format("wdl.gui.extensions.modVersion",
						name, mod.getVersion());
				
				if (mod instanceof IWDLModWithGui) {
					String buttonName = ((IWDLModWithGui) mod).getButtonName();
					if (buttonName == null || buttonName.isEmpty()) {
						buttonName = I18n.format("wdl.gui.extensions.defaultSettingsButtonText");
					}
					
					button = new GuiButton(0, 0, 0, 80, 20,
							((IWDLModWithGui) mod).getButtonName());
				}
			}
			
			@Override
			public void drawEntry(int slotIndex, int x, int y, int listWidth,
					int slotHeight, int mouseX, int mouseY,
					boolean isSelected) {
				if (button != null) {
					button.xPosition = GuiWDLExtensions.this.width - 92;
					button.yPosition = y - 1;
					
					button.drawButton(mc, mouseX, mouseY);
				}
				
				int centerY = y + slotHeight / 2
						- fontRendererObj.FONT_HEIGHT / 2;
				fontRendererObj.drawString(modDesc, x, centerY, 0xFFFFFF);
			}

			@Override
			public boolean mousePressed(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
				if (button != null) {
					if (button.mousePressed(mc, x, y)) {
						if (mod instanceof IWDLModWithGui) {
							((IWDLModWithGui) mod).openGui(GuiWDLExtensions.this);
						}
						
						button.playPressSound(mc.getSoundHandler());
					}
				}
				
				if (selectedModIndex != slotIndex) {
					selectedModIndex = slotIndex;
					
					mc.getSoundHandler().playSound(
							PositionedSoundRecord.createPositionedSoundRecord(
									new ResourceLocation("gui.button.press"),
									1.0F));
					
					updateDetailsList(mod);
					
					return true;
				}
				
				return false;
			}

			@Override
			public void mouseReleased(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
				if (button != null) {
					button.mouseReleased(x, y);
				}
			}
		}
		
		private List<IGuiListEntry> entries = new ArrayList<IGuiListEntry>() {{
			for (IWDLMod mod : WDLApi.getWDLMods().values()) {
				add(new ModEntry(mod));
			}
		}};
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.height = this.bottom = bottomLocation;
			
			super.drawScreen(mouseX, mouseY, partialTicks);
		}
		
		@Override
		public IGuiListEntry getListEntry(int index) {
			return entries.get(index);
		}

		@Override
		protected int getSize() {
			return entries.size();
		}
		
		@Override
		protected boolean isSelected(int slotIndex) {
			return slotIndex == selectedModIndex;
		}
		
		@Override
		public int getListWidth() {
			return GuiWDLExtensions.this.width - 20;
		}
		
		@Override
		protected int getScrollBarX() {
			return GuiWDLExtensions.this.width - 10;
		}
	}
	
	private class ModDetailList extends TextList {
		public ModDetailList() {
			super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width,
					GuiWDLExtensions.this.height - bottomLocation,
					MIDDLE_HEIGHT, BOTTOM_HEIGHT);
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			GL11.glTranslatef(0, bottomLocation, 0);
			
			this.height = GuiWDLExtensions.this.height - bottomLocation;
			this.bottom = this.height - 32;
			
			super.drawScreen(mouseX, mouseY, partialTicks);
			
			drawCenteredString(fontRendererObj,
					I18n.format("wdl.gui.extensions.detailsCaption"),
					GuiWDLExtensions.this.width / 2, 5, 0xFFFFFF);
			
			GL11.glTranslatef(0, -bottomLocation, 0);
		}
		
		/**
		 * Used by the drawing routine; edited to reduce weirdness.
		 * 
		 * (Don't move the bottom with the size of the screen).
		 */
		@Override
		protected void overlayBackground(int y1, int y2,
				int alpha1, int alpha2) {
			if (y1 == 0) {
				super.overlayBackground(y1, y2, alpha1, alpha2);
				return;
			} else {
				GL11.glTranslatef(0, -bottomLocation, 0);
				
				super.overlayBackground(y1 + bottomLocation, y2
						+ bottomLocation, alpha1, alpha2);
				
				GL11.glTranslatef(0, bottomLocation, 0);
			}
		}
	}
	
	private void updateDetailsList(IWDLMod selectedMod) {
		detailsList.clearLines();
		
		if (selectedMod != null) {
			String info = WDLApi.getModInfo(selectedMod);
			
			detailsList.addLine(info);
		}
	}
	
	/**
	 * Gui to display after this is closed.
	 */
	private final GuiScreen parent;
	/**
	 * List of mods.
	 */
	private ModList list;
	/**
	 * Details on the selected mod.
	 */
	private ModDetailList detailsList;
	
	public GuiWDLExtensions(GuiScreen parent) {
		this.parent = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		bottomLocation = height - 100;
		dragging = false;
		
		this.list = new ModList();
		this.detailsList = new ModDetailList();
		
		this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 29, I18n
				.format("gui.done")));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			mc.displayGuiScreen(parent);
		}
	}
	
	/**
	 * Whether the center section is being dragged.
	 */
	private boolean dragging = false;
	private int dragOffset;
	
	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		this.list.handleMouseInput();
		this.detailsList.handleMouseInput();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseY > bottomLocation && mouseY < bottomLocation + MIDDLE_HEIGHT) {
			dragging = true;
			dragOffset = mouseY - bottomLocation;
			
			return;
		}
		
		if (list.func_148179_a(mouseX, mouseY, mouseButton)) {
			return;
		}
		if (detailsList.func_148179_a(mouseX, mouseY, mouseButton)) {
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		dragging = false;
		
		if (list.func_148181_b(mouseX, mouseY, state)) {
			return;
		}
		if (detailsList.func_148181_b(mouseX, mouseY, state)) {
			return;
		}
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY,
			int clickedMouseButton, long timeSinceLastClick) {
		if (dragging) {
			bottomLocation = mouseY - dragOffset; 
		}
		
		//Clamp bottomLocation.
		if (bottomLocation < TOP_HEIGHT + 8) {
			bottomLocation = TOP_HEIGHT + 8;
		}
		if (bottomLocation > height - BOTTOM_HEIGHT - 8) {
			bottomLocation = height - BOTTOM_HEIGHT - 8;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		//Clamp bottomLocation.
		if (bottomLocation < TOP_HEIGHT + 33) {
			bottomLocation = TOP_HEIGHT + 33;
		}
		if (bottomLocation > height - MIDDLE_HEIGHT - BOTTOM_HEIGHT - 33) {
			bottomLocation = height - MIDDLE_HEIGHT - BOTTOM_HEIGHT - 33;
		}
		
		this.list.drawScreen(mouseX, mouseY, partialTicks);
		this.detailsList.drawScreen(mouseX, mouseY, partialTicks);
		
		this.drawCenteredString(this.fontRendererObj,
				I18n.format("wdl.gui.extensions.title"), this.width / 2, 8,
				0xFFFFFF);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
