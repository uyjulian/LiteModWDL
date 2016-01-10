package wdl.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.EntityUtils;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;

/**
 * GUI that controls what entities are saved.
 */
public class GuiWDLEntities extends GuiScreen {
	private class GuiEntityList extends GuiListExtended {
		/**
		 * Width of the largest entry.
		 */
		private int largestWidth;
		/**
		 * Width of an entire entry.
		 * 
		 * Equal to largestWidth + 255.
		 */
		private int totalWidth;
		
		private List<IGuiListEntry> entries = new ArrayList<IGuiListEntry>() {{
			try {
				int largestWidthSoFar = 0;
				
				Map<String, Collection<String>> entities = 
						EntityUtils.entitiesByGroup.asMap();
				for (Map.Entry<String, Collection<String>> e : entities
						.entrySet()) {
					CategoryEntry category = new CategoryEntry(e.getKey());
					add(category);

					for (String entity : e.getValue()) {
						add(new EntityEntry(category, entity));

						int width = fontRendererObj.getStringWidth(entity);
						if (width > largestWidthSoFar) {
							largestWidthSoFar = width;
						}
					}
				}
				
				largestWidth = largestWidthSoFar;
				totalWidth = largestWidth + 255;
			} catch (Exception e) {
				WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR,
						"wdl.messages.generalError.failedToSetUpEntityUI", e);
				
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		}};
		
		/**
		 * Provides a label.
		 * 
		 * Based off of 
		 * {@link net.minecraft.client.gui.GuiKeyBindingList.CategoryEntry}.
		 */
		private class CategoryEntry implements IGuiListEntry {
			private final String group;
			private final int labelWidth;
			
			private final GuiButton enableGroupButton; 
			
			private boolean groupEnabled;

			public CategoryEntry(String group) {
				this.group = group;
				this.labelWidth = mc.fontRendererObj.getStringWidth(group);
				
				this.groupEnabled = WDL.worldProps.getProperty("EntityGroup."
						+ group + ".Enabled", "true").equals("true");
				
				this.enableGroupButton = new GuiButton(0, 0, 0, 90, 18, 
						getButtonText());
			}

			@Override
			public void drawEntry(int slotIndex, int x, int y, int listWidth,
					int slotHeight, int mouseX, int mouseY,
					boolean isSelected) {
				mc.fontRendererObj.drawString(this.group, (x + 110 / 2)
						- (this.labelWidth / 2), y + slotHeight
						- mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFF);
				
				this.enableGroupButton.xPosition = x + 110;
				this.enableGroupButton.yPosition = y;
				this.enableGroupButton.displayString = getButtonText();
				
				this.enableGroupButton.drawButton(mc, mouseX, mouseY);
			}

			@Override
			public boolean mousePressed(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
				if (enableGroupButton.mousePressed(mc, x, y)) {
					groupEnabled ^= true;
					
					enableGroupButton.playPressSound(mc.getSoundHandler());
					
					this.enableGroupButton.displayString = getButtonText();
					
					WDL.worldProps.setProperty("EntityGroup." + group
							+ ".Enabled", Boolean.toString(groupEnabled));
					return true;
				}
				return false;
			}

			@Override
			public void mouseReleased(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
			}
			
			boolean isGroupEnabled() {
				return groupEnabled;
			}
			
			/**
			 * Gets the text for the on/off button.
			 */
			private String getButtonText() {
				if (groupEnabled) {
					return I18n.format("wdl.gui.entities.group.enabled");
				} else {
					return I18n.format("wdl.gui.entities.group.disabled");
				}
			}
		}
		
		/**
		 * Contains an actual entity's data.
		 * 
		 * Based off of 
		 * {@link net.minecraft.client.gui.GuiKeyBindingList.KeyEntry}.
		 */
		private class EntityEntry implements IGuiListEntry {
			private final CategoryEntry category;
			private final String entity;
			
			private final GuiButton onOffButton;
			private final GuiSlider rangeSlider;
			
			private boolean entityEnabled;
			private int range;
			
			private String cachedMode;
			
			public EntityEntry(CategoryEntry category, String entity) {
				this.category = category;
				this.entity = entity;
				
				entityEnabled = WDL.worldProps.getProperty("Entity." + entity + 
						".Enabled", "true").equals("true");
				range = EntityUtils.getEntityTrackDistance(entity);
				
				this.onOffButton = new GuiButton(0, 0, 0, 75, 18,
						getButtonText());
				this.onOffButton.enabled = category.isGroupEnabled();
				
				this.rangeSlider = new GuiSlider(1, 0, 0, 150, 18,
						"wdl.gui.entities.trackDistance", range, 256);
				
				this.cachedMode = mode;
				
				rangeSlider.enabled = (cachedMode.equals("user"));
			}

			@Override
			public void drawEntry(int slotIndex, int x, int y, int listWidth,
					int slotHeight, int mouseX, int mouseY,
					boolean isSelected) {
				//Center for everything but the labels.
				int center = (GuiWDLEntities.this.width / 2) - (totalWidth / 2)
						+ largestWidth + 10;
				
				mc.fontRendererObj.drawString(this.entity,
						center - largestWidth - 10, y + slotHeight / 2 - 
								mc.fontRendererObj.FONT_HEIGHT / 2, 0xFFFFFF);
				
				this.onOffButton.xPosition = center;
				this.onOffButton.yPosition = y;
				this.onOffButton.enabled = category.isGroupEnabled();
				this.onOffButton.displayString = getButtonText();
				
				this.rangeSlider.xPosition = center + 85;
				this.rangeSlider.yPosition = y;
				
				if (!this.cachedMode.equals(mode)) {
					cachedMode = mode;
					rangeSlider.enabled = (cachedMode.equals("user"));
					
					rangeSlider.setValue(EntityUtils
							.getEntityTrackDistance(entity));
				}
				
				this.onOffButton.drawButton(mc, mouseX, mouseY);
				this.rangeSlider.drawButton(mc, mouseX, mouseY);
			}

			@Override
			public boolean mousePressed(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
				if (onOffButton.mousePressed(mc, x, y)) {
					entityEnabled ^= true;
					
					onOffButton.playPressSound(mc.getSoundHandler());
					onOffButton.displayString = getButtonText();
					
					WDL.worldProps.setProperty("Entity." + entity + 
							".Enabled", Boolean.toString(entityEnabled));
					return true;
				}
				if (rangeSlider.mousePressed(mc, x, y)) {
					range = rangeSlider.getValue();
					
					WDL.worldProps.setProperty("Entity." + entity
							+ ".TrackDistance",
							Integer.toString(range));
					
					return true;
				}
				
				return false;
			}

			@Override
			public void mouseReleased(int slotIndex, int x, int y,
					int mouseEvent, int relativeX, int relativeY) {
				rangeSlider.mouseReleased(x, y);
				
				if (this.cachedMode.equals("user")) {
					range = rangeSlider.getValue();
					
					WDL.worldProps.setProperty("Entity." + entity
							+ ".TrackDistance",
							Integer.toString(range));
				}
			}
			
			/**
			 * Gets the text for the on/off button.
			 */
			private String getButtonText() {
				if (category.isGroupEnabled() && entityEnabled) {
					return I18n.format("wdl.gui.entities.entity.included");
				} else {
					return I18n.format("wdl.gui.entities.entity.ignored");
				}
			}
		}
		
		public GuiEntityList() {
			super(GuiWDLEntities.this.mc, GuiWDLEntities.this.width,
					GuiWDLEntities.this.height, 39,
					GuiWDLEntities.this.height - 32, 20);
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
		protected int getScrollBarX() {
			return (GuiWDLEntities.this.width) / 2 + (totalWidth / 2) + 10;
		}
	}
	
	private GuiEntityList entityList;
	private GuiScreen parent;
	
	private GuiButton rangeModeButton;
	private GuiButton presetsButton;
	
	private String mode;
	
	public GuiWDLEntities(GuiScreen parent) {
		this.parent = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100,
				this.height - 29, "OK"));
		
		rangeModeButton = new GuiButton(100, this.width / 2 - 155, 18, 150,
				20, getRangeModeText());
		presetsButton = new GuiButton(101, this.width / 2 + 5, 18, 150, 20, 
				I18n.format("wdl.gui.entities.rangePresets"));
		
		this.mode = WDL.worldProps.getProperty("Entity.TrackDistanceMode");
		
		presetsButton.enabled = shouldEnablePresetsButton();
		
		this.buttonList.add(rangeModeButton);
		this.buttonList.add(presetsButton);
		
		this.entityList = new GuiEntityList();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 100) {
			cycleRangeMode();
		}
		if (button.id == 101 && button.enabled) {
			mc.displayGuiScreen(new GuiWDLEntityRangePresets(this));
		}
		if (button.id == 200) {
			mc.displayGuiScreen(parent);
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
		this.entityList.handleMouseInput();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (entityList.func_148179_a(mouseX, mouseY, mouseButton)) {
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (entityList.func_148181_b(mouseX, mouseY, state)) {
			return;
		}
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.entityList.drawScreen(mouseX, mouseY, partialTicks);
		
		this.drawCenteredString(this.fontRendererObj,
				I18n.format("wdl.gui.entities.title"), this.width / 2, 8,
				0xFFFFFF);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Cycles the range mode value.
	 */
	private void cycleRangeMode() {
		if (mode.equals("default")) {
			if (WDLPluginChannels.hasServerEntityRange()) {
				mode = "server";
			} else {
				mode = "user";
			}
		} else if (mode.equals("server")) {
			mode = "user";
		} else {
			mode = "default";
		}
		
		WDL.worldProps.setProperty("Entity.TrackDistanceMode", mode);
		
		rangeModeButton.displayString = getRangeModeText();
		presetsButton.enabled = shouldEnablePresetsButton();
	}
	
	/**
	 * Gets the text for the range mode button.
	 */
	private String getRangeModeText() {
		String mode = WDL.worldProps.getProperty("Entity.TrackDistanceMode");
		
		return I18n.format("wdl.gui.entities.trackDistanceMode." + mode);
	}
	
	/**
	 * Is the current mode a mode where the presets button should be enabled?
	 */
	private boolean shouldEnablePresetsButton() {
		return mode.equals("user");
	}
}
