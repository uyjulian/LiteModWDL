package wdl.litemod;

import wdl.WDL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenInject extends GuiIngameMenu {
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		// Your own code //
		wdl.WDLHooks.injectWDLButtons(this, buttonList);
	}
	
}
