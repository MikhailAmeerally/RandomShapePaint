package ca.utoronto.utm.paint;
import java.awt.Graphics2D;

public class RectangleCommand implements PaintCommand {
	private Rectangle rectangle;
	public RectangleCommand(Rectangle rectangle){
		this.rectangle = rectangle;
	}
	public void execute(Graphics2D g2d){
		g2d.setColor(rectangle.getColor());
		Point topLeft = this.rectangle.getTopLeft();
		Point dimensions = this.rectangle.getDimensions();
		if(rectangle.isFilled()){
			g2d.fillRect(topLeft.x, topLeft.y, dimensions.x, dimensions.y);
		} else {
			g2d.drawRect(topLeft.x, topLeft.y, dimensions.x, dimensions.y);
		}
	}
	
	public String toString(){
		String s = "Rectangle\n";
		int r = this.rectangle.getColor().getRed();
		int g = this.rectangle.getColor().getGreen();
		int b = this.rectangle.getColor().getBlue();
		s += "\tcolor:" + r + "," + g + "," + b + "\n";
		s += "\tfilled:" + this.rectangle.isFilled() + "\n";
		s += "\tp1:" + this.rectangle.getTopLeft() + "\n";
		s += "\tp2:" + this.rectangle.getBottomRight() + "\n";
		s += "EndRectangle\n";
		return s;
	}
	
}
