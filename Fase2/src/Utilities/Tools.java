package Utilities;

import robocode.ScannedRobotEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static robocode.util.Utils.normalRelativeAngle;

public class Tools {
    private final ArrayList<Point> quadrants;
    double battleFieldWidth;
    double battleFieldHeight;

    public Tools(){
        quadrants =  new ArrayList<>(Arrays.asList(new Point(0,0), new Point(0,1),
                new Point(1,1), new Point(1,0)));
    }

    public void setDimensions(double w, double h){
        battleFieldWidth = w;
        battleFieldHeight = h;
    }

    public int getMyQuad(Point q){
        return quadrants.indexOf(q);
    }

    public Point getMyQuad(int q){
        return quadrants.get(q);
    }

    public double getBattleFieldWidth() {
        return battleFieldWidth;
    }
    public double getBattleFieldHeight() {
        return battleFieldHeight;
    }

    public Point getBattleFieldDimensions(){
        return new Point(battleFieldWidth, battleFieldHeight);
    }

    public Point homeFromQuad(Point p, int pix){
        return new Point(p.getX()==0? pix : battleFieldWidth-pix, p.getY()==0? pix : battleFieldHeight-pix );
    }

    public Point getRobotCoordinates(ScannedRobotEvent e, double x, double y, double h){
        Point p = getCartesianFromPolar(e.getBearing()-h, e.getDistance()+euclidianDistance(x, y, 0, 0));
        System.out.println(p+" :" + e.getName());
        return p;
    }

    // Heading em radianos!
    public static double getAngle(Point p, Point me, Double heading ){
        double dx = p.getX() - me.getX();
        double dy = p.getY() - me.getY();

        return Math.toDegrees(normalRelativeAngle(Math.atan2(dx,dy)-heading));
    }

    public Point getCartesianFromPolar(double angle, double distance) {
        double rads = (java.lang.Math.PI/180)*angle;
        double cos = Math.cos(rads)*distance;
        double sin = Math.sin(rads)*distance;
        return new Point(sin,cos);

    }

    public static double euclidianDistance(double x1, double y1, double x2, double y2) {
        return java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
    }

}
