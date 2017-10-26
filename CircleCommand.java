package ca.utoronto.utm.paint;
import java.awt.Graphics2D;

public class CircleCommand implements PaintCommand {
	private Circle circle;
	public CircleCommand(Circle circle){
		this.circle=circle;
	}
	public void execute(Graphics2D g2d){
		g2d.setColor(circle.getColor());
		int x = this.circle.getCentre().x;
		int y = this.circle.getCentre().y;
		int radius = this.circle.getRadius();
		if(circle.isFilled()){
			g2d.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g2d.drawOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
	
	public String toString(){
		String s = "Circle\n";
		int r = this.circle.getColor().getRed();
		int g = this.circle.getColor().getGreen();
		int b = this.circle.getColor().getBlue();
		s += "\tcolor:" + r + "," + g + "," + b + "\n";
		s += "\tfilled:" + this.circle.isFilled() + "\n";
		s += "\tcenter:" + this.circle.getCentre() + "\n";
		s += "\tradius:" + this.circle.getRadius() + "\n";
		s += "EndCircle\n";
		return s;
	}
}
