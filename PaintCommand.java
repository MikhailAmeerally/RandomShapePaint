package ca.utoronto.utm.paint;
import java.awt.Graphics2D;

public interface PaintCommand {
	public void execute(Graphics2D g2d);
}
