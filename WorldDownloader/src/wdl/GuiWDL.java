package wdl;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiWDL extends GuiScreen
{
    private String title = "";
    private GuiScreen parent;
    private GuiTextField worldName;
    private GuiButton autoStartBtn;
    private GuiButton worldOverrides;
    private GuiButton generatorOverrides;
    private GuiButton playerOverrides;

    public GuiWDL(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	@SuppressWarnings("unchecked")
	public void initGui()
    {
        if (WDL.isMultiworld && WDL.worldName.isEmpty())
        {
            this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(this.parent));
        }

        if (!WDL.propsFound)
        {
            this.mc.displayGuiScreen(new GuiWDLMultiworld(this.parent));
        }
        else
        {
            this.buttonList.clear();
            this.title = "Options for " + WDL.baseFolderName.replace('@', ':');
            int w = this.width / 2;
            int h = this.height / 4;
            int hi = h - 15;

            if (WDL.baseProps.getProperty("ServerName").isEmpty())
            {
                WDL.baseProps.setProperty("ServerName", WDL.getServerName());
            }

            this.worldName = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 70, hi, 168, 18);
            this.updateServerName(false);
            hi += 22;
            this.autoStartBtn = new GuiButton(1, w - 100, hi, "Start Download: ERROR");
            this.buttonList.add(this.autoStartBtn);
            this.updateAutoStart(false);
            hi += 28;
            this.worldOverrides = new GuiButton(4, w - 100, hi, "World Overrides...");
            this.buttonList.add(this.worldOverrides);
            hi += 22;
            this.generatorOverrides = new GuiButton(5, w - 100, hi, "World Generator Overrides...");
            this.buttonList.add(this.generatorOverrides);
            hi += 22;
            this.playerOverrides = new GuiButton(6, w - 100, hi, "Player Overrides...");
            this.buttonList.add(this.playerOverrides);
            hi += 28;
            this.buttonList.add(new GuiButton(100, w - 100, h + 150, "Done"));
        }
    }

    @Override
	protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            this.updateServerName(true);

            if (guibutton.id == 1)
            {
                this.updateAutoStart(true);
            }
            else if (guibutton.id == 4)
            {
                this.mc.displayGuiScreen(new GuiWDLWorld(this));
            }
            else if (guibutton.id == 5)
            {
                this.mc.displayGuiScreen(new GuiWDLGenerator(this));
            }
            else if (guibutton.id == 6)
            {
                this.mc.displayGuiScreen(new GuiWDLPlayer(this));
            }
            else if (guibutton.id == 100)
            {
                WDL.saveProps();
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     * @throws IOException 
     */
    @Override
	protected void mouseClicked(int var1, int var2, int var3) throws IOException
    {
        super.mouseClicked(var1, var2, var3);
        this.worldName.mouseClicked(var1, var2, var3);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     * @throws IOException 
     */
    @Override
	protected void keyTyped(char c, int i) throws IOException
    {
        super.keyTyped(c, i);
        this.worldName.textboxKeyTyped(c, i);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
	public void updateScreen()
    {
        this.worldName.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
	public void drawScreen(int var1, int var2, float var3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 4 - 40, 16777215);
        this.drawString(this.fontRendererObj, "Name:", this.width / 2 - 99, this.height / 4 - 10, 16777215);
        this.worldName.drawTextBox();
        super.drawScreen(var1, var2, var3);
    }

    public void updateAutoStart(boolean btnClicked)
    {
        String autoStart = WDL.baseProps.getProperty("AutoStart");

        if (autoStart.equals("true"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("AutoStart", "false");
                this.updateAutoStart(false);
            }
            else
            {
                this.autoStartBtn.displayString = "Start Download: Automatically";
            }
        }
        else if (btnClicked)
        {
            WDL.baseProps.setProperty("AutoStart", "true");
            this.updateAutoStart(false);
        }
        else
        {
            this.autoStartBtn.displayString = "Start Download: Only in menu";
        }
    }

    private void updateServerName(boolean var1)
    {
        if (var1)
        {
            WDL.baseProps.setProperty("ServerName", this.worldName.getText());
        }
        else
        {
            this.worldName.setText(WDL.baseProps.getProperty("ServerName"));
        }
    }
}
