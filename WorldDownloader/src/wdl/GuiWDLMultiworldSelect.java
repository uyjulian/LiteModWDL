package wdl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiWDLMultiworldSelect extends GuiScreen
{
    private GuiButton cancelBtn;
    private GuiTextField newNameField;
    private boolean newWorld = false;
    private int positionID;
    private int thirdPersonViewSave;
    private GuiButton[] buttons;
    private String[] worlds;
    private GuiScreen parent;

    public GuiWDLMultiworldSelect(GuiScreen var1)
    {
        this.parent = var1;
        this.thirdPersonViewSave = WDL.mc.gameSettings.thirdPersonView;
        WDL.mc.gameSettings.thirdPersonView = 0;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	public void initGui()
    {
        this.buttonList.clear();
        int var1 = this.width / 2;
        int var2 = this.height / 4;
        int var3 = this.width / 150;

        if (var3 == 0)
        {
            var3 = 1;
        }

        int var4 = this.width / var3 - 5;
        this.cancelBtn = new GuiButton(100, var1 - 100, this.height - 30, "Cancel");
        this.buttonList.add(this.cancelBtn);
        String var5 = WDL.baseProps.getProperty("LinkedWorlds");
        String[] var6 = var5.split("[|]");
        String[] var7 = new String[var6.length];
        int var8 = 0;
        int var9;

        for (var9 = 0; var9 < var6.length; ++var9)
        {
            if (var6[var9].isEmpty())
            {
                var6[var9] = null;
            }
            else
            {
                Properties var12 = WDL.loadWorldProps(var6[var9]);

                if (var12 == null)
                {
                    var6[var9] = null;
                }
                else
                {
                    ++var8;
                    var7[var9] = var12.getProperty("WorldName");
                }
            }
        }

        if (var3 > var8 + 1)
        {
            var3 = var8 + 1;
        }

        var9 = (this.width - var3 * var4) / 2;
        this.worlds = new String[var8];
        this.buttons = new GuiButton[var8 + 1];
        int var121 = 0;
        int var11;

        for (var11 = 0; var11 < var6.length; ++var11)
        {
            if (var6[var11] != null)
            {
                this.worlds[var121] = var6[var11];
                this.buttons[var121] = new GuiButton(var121, var121 % var3 * var4 + var9, this.height - 60 - var121 / var3 * 21, var4, 20, var7[var11]);
                this.buttonList.add(this.buttons[var121]);
                ++var121;
            }
        }

        var11 = this.buttons.length - 1;

        if (!this.newWorld)
        {
            this.buttons[var11] = new GuiButton(var11, var11 % var3 * var4 + var9, this.height - 60 - var11 / var3 * 21, var4, 20, "< New Name >");
            this.buttonList.add(this.buttons[var11]);
        }

        this.newNameField = new GuiTextField(0, this.fontRendererObj, var11 % var3 * var4 + var9, this.height - 60 - var11 / var3 * 21 + 1, var4, 18);
    }

    @Override
	protected void actionPerformed(GuiButton var1)
    {
        if (var1.enabled)
        {
            this.newWorld = false;

            if (var1.id == this.worlds.length)
            {
                this.newWorld = true;
                this.buttonList.remove(this.buttons[this.worlds.length]);
            }
            else if (var1.id == 100)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
            }
            else
            {
                this.worldSelected(this.worlds[var1.id]);
            }
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
	protected void mouseClicked(int var1, int var2, int var3)
    {
        try
        {
            super.mouseClicked(var1, var2, var3);
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
        }

        if (this.newWorld)
        {
            this.newNameField.mouseClicked(var1, var2, var3);
        }
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    @Override
	protected void keyTyped(char var1, int var2)
    {
        try
        {
            super.keyTyped(var1, var2);
        }
        catch (IOException var4)
        {
            var4.printStackTrace();
        }

        if (this.newNameField.isFocused())
        {
            this.newNameField.textboxKeyTyped(var1, var2);

            if (var2 == 28)
            {
                String var3 = this.newNameField.getText();

                if (var3 != null && !var3.isEmpty())
                {
                    this.worldSelected(this.addMultiworld(var3));
                }
            }
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
	public void updateScreen()
    {
        this.newNameField.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
	public void drawScreen(int var1, int var2, float var3)
    {
        drawRect(this.width / 2 - 120, 0, this.width / 2 + 120, this.height / 16 + 25, -1073741824);

        if (this.parent == null)
        {
            this.drawCenteredString(this.fontRendererObj, "World Downloader - Trying To Start Download", this.width / 2, this.height / 16, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, "World Downloader - Trying To Change Options", this.width / 2, this.height / 16, 16777215);
        }

        this.drawCenteredString(this.fontRendererObj, "Where are you?", this.width / 2, this.height / 16 + 10, 16711680);
        float var4 = 0.475F;
        float var5 = 1.0F;

        if (this.newWorld)
        {
            this.newNameField.drawTextBox();
        }

        super.drawScreen(var1, var2, var3);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
	public void onGuiClosed()
    {
        super.onGuiClosed();
        WDL.mc.gameSettings.thirdPersonView = this.thirdPersonViewSave;
    }

    private void worldSelected(String var1)
    {
        WDL.worldName = var1;
        WDL.isMultiworld = true;
        WDL.propsFound = true;

        if (this.parent == null)
        {
            WDL.start();
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            WDL.worldProps = WDL.loadWorldProps(var1);
            this.mc.displayGuiScreen(new GuiWDL(this.parent));
        }
    }

    private String addMultiworld(String var1)
    {
        String var2 = var1;
        String var3 = "\\/:*?\"<>|";
        char[] var4 = var3.toCharArray();
        int var5 = var4.length;
        int var6;

        for (var6 = 0; var6 < var5; ++var6)
        {
            char var11 = var4[var6];
            var2 = var2.replace(var11, '_');
        }

        (new File(this.mc.mcDataDir, "saves/" + WDL.baseFolderName + " - " + var2)).mkdirs();
        Properties var141 = new Properties(WDL.baseProps);
        var141.setProperty("WorldName", var1);
        String[] var12 = new String[this.worlds.length + 1];

        for (var6 = 0; var6 < this.worlds.length; ++var6)
        {
            var12[var6] = this.worlds[var6];
        }

        var12[var12.length - 1] = var2;
        String var13 = "";
        String[] var14 = var12;
        int var8 = var12.length;

        for (int var9 = 0; var9 < var8; ++var9)
        {
            String var10 = var14[var9];
            var13 = var13 + var10 + "|";
        }

        WDL.baseProps.setProperty("LinkedWorlds", var13);
        WDL.saveProps(var2, var141);
        return var2;
    }
}
