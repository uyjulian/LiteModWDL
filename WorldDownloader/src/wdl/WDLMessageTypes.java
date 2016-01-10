package wdl;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import wdl.api.IMessageTypeAdder;
import wdl.api.IWDLMessageType;

/**
 * Enum containing WDL's default {@link IWDLMessageType}s.
 * 
 * <b>Mostly intended for internal use.</b> Extensions may use {@link #INFO} and
 * {@link #ERROR}, but if they need something more complex, they should
 * implement {@link IMessageTypeAdder} and create new ones with that unless
 * it's a perfect fit.
 */
public enum WDLMessageTypes implements IWDLMessageType {
	INFO("wdl.messages.message.info", EnumChatFormatting.RED,
			EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
	ERROR("wdl.messages.message.error", EnumChatFormatting.DARK_GREEN,
			EnumChatFormatting.DARK_RED, true,
			MessageTypeCategory.CORE_RECOMMENDED),
	UPDATES("wdl.messages.message.updates", EnumChatFormatting.RED,
			EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
	LOAD_TILE_ENTITY("wdl.messages.message.loadingTileEntity", false),
	ON_WORLD_LOAD("wdl.messages.message.onWorldLoad",false),
	ON_BLOCK_EVENT("wdl.messages.message.blockEvent", true),
	ON_MAP_SAVED("wdl.messages.message.mapDataSaved", false),
	ON_CHUNK_NO_LONGER_NEEDED("wdl.messages.message.chunkUnloaded", false), 
	ON_GUI_CLOSED_INFO("wdl.messages.message.guiClosedInfo", true),
	ON_GUI_CLOSED_WARNING("wdl.messages.message.guiClosedWarning", true),
	SAVING("wdl.messages.message.saving", true),
	REMOVE_ENTITY("wdl.messages.message.removeEntity", false),
	PLUGIN_CHANNEL_MESSAGE("wdl.messages.message.pluginChannel", false),
	UPDATE_DEBUG("wdl.messages.message.updateDebug", false);
	
	/**
	 * Constructor with the default values for a debug message.
	 */
	private WDLMessageTypes(String i18nKey,
			boolean enabledByDefault) {
		this(i18nKey, EnumChatFormatting.DARK_GREEN,
				EnumChatFormatting.GOLD, enabledByDefault,
				MessageTypeCategory.CORE_DEBUG);
	}
	/**
	 * Constructor that allows specification of all values.
	 */
	private WDLMessageTypes(String i18nKey, EnumChatFormatting titleColor,
			EnumChatFormatting textColor, boolean enabledByDefault,
			MessageTypeCategory category) {
		this.displayTextKey = i18nKey + ".text";
		this.titleColor = titleColor;
		this.textColor = textColor;
		this.descriptionKey = i18nKey + ".description";
		this.enabledByDefault = enabledByDefault;
		
		WDLMessages.registerMessage(this.name(), this, category);
	}
	
	/**
	 * I18n key for the text to display on a button for this enum value.
	 */
	private final String displayTextKey;
	/**
	 * Format code for the '[WorldDL]' label.
	 */
	private final EnumChatFormatting titleColor;
	/**
	 * Format code for the text after the label.
	 */
	private final EnumChatFormatting textColor;
	/**
	 * I18n key for the description text.
	 */
	private final String descriptionKey;
	/**
	 * Whether this type of message is enabled by default.
	 */
	private final boolean enabledByDefault;
	
	@Override
	public String getDisplayName() {
		return I18n.format(displayTextKey);
	}

	@Override
	public EnumChatFormatting getTitleColor() {
		return titleColor;
	}
	
	@Override
	public EnumChatFormatting getTextColor() {
		return textColor;
	}

	@Override
	public String getDescription() {
		return I18n.format(descriptionKey);
	}
	
	@Override
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}
}