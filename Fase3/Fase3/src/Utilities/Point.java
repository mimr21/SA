package Utilities;


import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double distance(Point point) {
        return java.lang.Math.sqrt(((java.lang.Math.pow((point.getX() - x), 2)) + (java.lang.Math.pow((point.getY() - y), 2))));
    }
}
