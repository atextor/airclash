package airclash.system.console;

import java.io.IOException;

import org.fenggui.console.Console;
import org.fenggui.render.Font;
import org.fenggui.render.Graphics;
import org.fenggui.render.ICarretRenderer;
import org.fenggui.render.IOpenGL;
import org.fenggui.render.ITextRenderer;
import org.fenggui.render.LineCarretRenderer;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOutputStream;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;
import org.fenggui.util.Timer;

/**
 * The class determines the console appearance. Unfortunately, all the important
 * fields in org.fenggui.console.ConsoleAppearance (namely textColor) are private.
 * Subclassing alone doesn't work, so this is a proxy class: All methods are relayed
 * to the actual ConsoleAppearance object, only the paintContent method is overridden
 * so that the font color can be changed there. 
 * @author Andreas Textor
 */
public class ConsoleAppearance extends org.fenggui.console.ConsoleAppearance {
	/** The caret renderer. */
	private ICarretRenderer caretRenderer = null;

	/** The text color. */
	private Color textColor = Color.WHITE;

	/** The proxied master object. */
	private org.fenggui.console.ConsoleAppearance master;

	/**
	 * Constructor.
	 * @param pCon The console widget
	 * @param pMaster The proxied master object
	 */
	public ConsoleAppearance(final Console pCon,
			final org.fenggui.console.ConsoleAppearance pMaster) {
		super(pCon);
		this.master = pMaster;
		this.caretRenderer = new LineCarretRenderer(Font.getDefaultFont().getHeight());
	}

	/**
	 * Paints the console. This method basically contains the same calls as the
	 * original method (this.master.paintContent(Graphics, IOpenGL)), but here
	 * we can change the text color.
	 * @param pGraphics The graphics object
	 * @param pGL The OpenGL context
	 */
	@Override
	public void paintContent(final Graphics pGraphics, final IOpenGL pGL) {
		pGraphics.setColor(this.textColor);
		this.master.getPromtRenderer().render(0, 0, pGraphics, pGL);
		if (this.master.getCarretTimer().getState() == 0
				&& this.master.getWidget().hasFocus()) {
			this.master.getPromtRenderer().renderCarret(0, 0,
					this.master.getWidget().getCarretIndex() - 1,
					this.caretRenderer, pGraphics, pGL);
		}
		this.master.getTextRenderer().render(0, this.master.getPromtRenderer().getHeight(),
				pGraphics, pGL);
	}
	
	// Relayed Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getContentMinSizeHint() {
		return this.master.getContentMinSizeHint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITextRenderer getTextRenderer() {
		return this.master.getTextRenderer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITextRenderer getPromtRenderer() {
		return this.master.getPromtRenderer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCarretRenderer(final ICarretRenderer pCarretRenderer) {
		this.master.setCarretRenderer(pCarretRenderer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Console getWidget() {
		return this.master.getWidget();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timer getCarretTimer() {
		return this.master.getCarretTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(final InputOutputStream pStream) throws IOException,
			IXMLStreamableException {
		this.master.process(pStream);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFont(final Font pFont) {
		this.master.setFont(pFont);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getFont() {
		return this.master.getFont();
	}

}
