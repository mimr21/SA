import robocode.AdvancedRobot;
import robocode.Condition;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Odom extends Condition {
    private ArrayList<Point2D> points= new ArrayList<>();
    private double total=0.0D;
    private AdvancedRobot r;
    private Point2D goal;
    private boolean isRunning=false;
    private boolean isFinished=false;

    public Odom(String condition, AdvancedRobot r,Point2D goal ) {
        super(condition);
        this.r=r;
        this.goal= goal;
    }

    @Override
    public boolean test() {
        Point2D thisPoint = new Point2D.Double(r.getX(), r.getY());

        if(!isRunning){

            isRunning= !isFinished && thisPoint.distance(goal)<0.5;
            //isRunning= !isFinished && ((int)thisPoint.getX())==((int)goal.getX()) && ((int)thisPoint.getY())==((int)goal.getY());

        }
        if(isRunning){
            isFinished=  thisPoint.distance(goal)<0.5 && points.size()>50;
            if(isFinished) isRunning=false;
            if(!points.isEmpty()){
                Point2D lastPoint = points.get(points.size() - 1);
                total += lastPoint.distance(thisPoint);
            }
            points.add(thisPoint);
        }
        this.r.setDebugProperty("isRunning", String.valueOf(isRunning));
        this.r.setDebugProperty("Distance", total + " Pixels");
        this.r.setDebugProperty("isFinished", String.valueOf(isFinished));
        
        return isFinished;
    }

    public double getDistance() {
        this.r.setDebugProperty("Distance", total + " Pixels");
        return total;
    }
}
