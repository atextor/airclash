package airclash.system.gfx;

import java.io.IOException;

import org.fenggui.Button;
import org.fenggui.PixmapDecorator;
import org.fenggui.render.Binding;
import org.fenggui.render.Pixmap;
import org.fenggui.util.Spacing;

import airclash.system.exceptions.InvalidResourceException;

/**
 * A pixmap-decorated button for menus.
 * @author Andreas Textor
 */
public class GuiButton extends Button {
	/**
	 * Constructor: This sets up the layout for the button. All other things
	 * (like ButtonPressedListener) should be added like with normal buttons.
	 * @param pImage The image name. data/images/NAME_{pressed,default,highlight}.png
	 * are read.
	 * @throws InvalidResourceException Thrown when one of the images can't be found
	 */
	public GuiButton(final String pImage) throws InvalidResourceException {
		super();
		
		try {
			final Pixmap pressedPic = new Pixmap(Binding.getInstance()
					.getTexture("data/images/" + pImage + "_pressed.png"));
			final Pixmap defaultPic = new Pixmap(Binding.getInstance()
					.getTexture("data/images/" + pImage + "_default.png"));
			final Pixmap hoverPic   = new Pixmap(Binding.getInstance()
					.getTexture("data/images/" + pImage + "_highlight.png"));
			setSize(defaultPic.getWidth(), defaultPic.getHeight());
			getAppearance().setPadding(Spacing.ZERO_SPACING);
			getAppearance().setMargin(Spacing.ZERO_SPACING);
			getAppearance().removeAll();
			
			getAppearance().add(new PixmapDecorator(Button.LABEL_DEFAULT, defaultPic));
			getAppearance().add(new PixmapDecorator(Button.LABEL_MOUSEHOVER, hoverPic));
			getAppearance().add(new PixmapDecorator(Button.LABEL_PRESSED, pressedPic));
			
			getAppearance().setEnabled(Button.LABEL_PRESSED, false);
			getAppearance().setEnabled(Button.LABEL_MOUSEHOVER, false);
			getAppearance().setEnabled(Button.LABEL_DEFAULT, true);
		} catch (IOException e) {
			throw new InvalidResourceException(e);
		}
	}
}
