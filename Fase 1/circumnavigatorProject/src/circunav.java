

import robocode.*;
import standardOdometer.Odometer;

public class circunav extends AdvancedRobot {
    private final Odometer odometer = new Odometer("IsRacing", this);



    public void run() {

        double estimated;
        ahead(10);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true){
            if((estimated = pointSomewhere(getX(),getY(),18,18))>1 )
                ahead(estimated);
            else{

            }
        }


    }

    private void referentialFinder() {
        double myHead =getHeading();
        turnLeft(myHead);
    }

    private double pointSomewhere(double fromX, double fromY, double toX, double toY) {
        double xft=fromX-toX, yft= fromY-toY;
        double heading = Math.toRadians(getHeading());

        double xhead=Math.sin(heading), yhead=Math.cos(heading);

        double vect = (xft*xhead+yft*yhead);
        double norm = Math.sqrt(
                (Math.pow(xft,2)+Math.pow(yft,2)))*
                Math.sqrt(
                (Math.pow(xhead,2)+Math.pow(yhead,2))
        );
        double rotateby=Math.acos(vect/norm);
        System.out.println("RotateBy:" +Math.toDegrees(rotateby));
        turnLeft(Math.toDegrees(rotateby));
        return norm;


    }


    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }
    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("IsRacing"))
            this.odometer.getRaceDistance();
    }
}
