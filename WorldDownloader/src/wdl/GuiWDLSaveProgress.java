package wdl;

import java.io.IOException;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IProgressUpdate;

/**
 * GUI screen shown while the world is being saved.
 * <br/>
 * Based off of vanilla minecraft's 
 * {@link net.minecraft.client.gui.GuiScreenWorking GuiScreenWorking}.
 */
public class GuiWDLSaveProgress extends GuiScreen {
	private final String title;
	private String majorTaskMessage = "";
	private String minorTaskMessage = "";
	private int majorTaskNumber;
	private final int majorTaskCount;
	private int minorTaskProgress;
	private int minorTaskMaximum;
	
	private boolean doneWorking = false;
	
	/**
	 * Creates a new GuiWDLSaveProgress.
	 * 
	 * @param title The title.
	 * @param taskCount The total number of major tasks that there will be.
	 */
	public GuiWDLSaveProgress(String title, int taskCount) {
		this.title = title;
		this.majorTaskCount = taskCount;
		this.majorTaskNumber = 0;
	}
	
	/**
	 * Starts a new major task with the given message.
	 */
	public void startMajorTask(String message, int minorTaskMaximum) {
		this.majorTaskMessage = message;
		this.majorTaskNumber++;
		
		this.minorTaskMessage = "";
		this.minorTaskProgress = 0;
		this.minorTaskMaximum = minorTaskMaximum;
	}

	/**
	 * Updates the progress on the current minor task.
	 * 
	 * @param message
	 *            The message -- should be something like "saving chunk at x,z";
	 *            the current position and maximum and the percent are
	 *            automatically appended after it.
	 */
	public void setMinorTaskProgress(String message, int progress) {
		this.minorTaskMessage = message;
		this.minorTaskProgress = progress;
	}
	
	/**
	 * Updates the progress on the minor task.
	 */
	public void setMinorTaskProgress(int progress) {
		this.minorTaskProgress = progress;
	}

	/**
	 * Sets the GUI as done working, meaning it will be closed next tick.
	 */
	public void setDoneWorking() {
		this.doneWorking = true;
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (this.doneWorking) {
			this.mc.displayGuiScreen((GuiScreen) null);
		} else {
			this.drawBackground(0);
			
			String majorProgressInfo = "";
			if (majorTaskCount > 1) {
				majorProgressInfo = " (" + majorTaskNumber + " of " + 
						majorTaskCount + ")";
			}
			String minorProgressInfo = "";
			if (minorTaskMaximum > 1) {
				minorProgressInfo = " (" + minorTaskProgress + " of " + 
						minorTaskMaximum + ")";
			}
			
			this.drawCenteredString(this.fontRendererObj, this.title,
					this.width / 2, 70, 0xFFFFFF);
			
			this.drawCenteredString(this.fontRendererObj, this.majorTaskMessage
					+ majorProgressInfo, this.width / 2, 100, 0xFFFFFF);
			
			if (minorTaskMaximum > 0) {
				this.drawProgressBar(110, 84, 89, 
						(majorTaskNumber * minorTaskMaximum) + minorTaskProgress, 
						(majorTaskCount + 1) * minorTaskMaximum);
			} else {
				this.drawProgressBar(110, 84, 89, majorTaskNumber, 
						majorTaskCount);
			}
			
			this.drawCenteredString(this.fontRendererObj, this.minorTaskMessage
					+ minorProgressInfo, this.width / 2, 130, 0xFFFFFF);
			this.drawProgressBar(140, 64, 69, minorTaskProgress, minorTaskMaximum);
			
			super.drawScreen(mouseX, mouseY, partialTicks);
		}
	}
	
	/**
	 * Draws a progress bar on the screen. (A lot of things are always kept the
	 * same and thus aren't arguments, such as x-position being the center of
	 * the screen).
	 * 
	 * @param y
	 *            Y-position of the progress bar.
	 * @param emptyV
	 *            The vertical coordinate of the empty part in the icon map
	 *            (icons.png)
	 * @param filledV
	 *            The vertical coordinate of the full part in the icon map
	 *            (icons.png)
	 * @param progress
	 *            The progress into the bar.
	 * @param maximum
	 *            The maximum value of progress.
	 */
	private void drawProgressBar(int y, int emptyV, int filledV, 
			int progress, int maximum) {
		if (maximum == 0) {
			return;
		}
		
		this.mc.getTextureManager().bindTexture(Gui.icons);
		
		final int fullWidth = 182;
		final int currentWidth = (progress * fullWidth) / maximum;
		final int height = 5;
		
		final int x = (this.width / 2) - (fullWidth / 2);
		final int u = 0; //Texture position.
		
		drawTexturedModalRect(x, y, u, emptyV, fullWidth, height);
		drawTexturedModalRect(x, y, u, filledV, currentWidth, height);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		//Don't call the super method, as that causes the UI to close if escape
		//is pressed.
	}
}
