import robocode.*;
import standardOdometer.Odometer;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class CircumNav extends AdvancedRobot {
    private final Odometer odometer = new Odometer("IsRacing", this);


    public void run() {
        goTo(18,18);
    }


    void goTo(double toX, double toY){
        double fromX = getX();
        double fromY = getY();
        double dist =  euclidianDistance(getX(), fromY, toX, toY);

        // calculate the angle
        double complementaryAngle = getAngle(fromX, fromY, toX, toY);
        double turningAngle = 180-complementaryAngle;

        // Turn to initial position
        turnLeft(normalRelativeAngleDegrees(turningAngle + getHeading()));

        ahead(dist);
        turnRight(360-getHeading());
    }

    // calculates the complementary angle aka the arc cosine of an angle
    private static double getAngle(double fromX, double fromY, double toX, double toY){
        // pythagoras theorem
        double h = euclidianDistance(fromX, fromY, toX, toY);
        double adj = euclidianDistance(fromX, fromY, fromX, toY);

        double cosAlpha = adj/h;

        // arc cosine aka inverse of cosine
        double acosAlpha = java.lang.Math.acos(cosAlpha);

        // transform to degrees
        double acosAlphaDegrees = (180/java.lang.Math.PI)*acosAlpha;

        return acosAlphaDegrees;
    }

    private static double euclidianDistance(double x1, double y1, double x2, double y2) {
        double dist = java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
        return dist;
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
