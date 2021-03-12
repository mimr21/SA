package Utilities;


import java.io.Serializable;

public class Point implements Serializable {
    double x;
    double y;
    private static final long serialVersionUID = 1L;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        x=p.getX();
        y=p.getY();
    }
    public static Point toCartesian(Point from ,double dist, double heading){
        double enemyX = from.getX() + dist * Math.sin(Math.toRadians(heading));
        double enemyY = from.getY() + dist * Math.cos(Math.toRadians(heading));
        return new Point(enemyX, enemyY);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
