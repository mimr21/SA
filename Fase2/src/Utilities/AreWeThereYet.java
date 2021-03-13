package Utilities;

import robocode.Condition;
import robocode.Robot;

public class AreWeThereYet extends Condition {
    private Point goal;
    private Robot r;

    public AreWeThereYet(Robot r, Point p) {
        this.r = r;
        goal=p;
    }

    @Override
    public boolean test() {
        return (goal.distance(new Point(r.getX(),r.getY()))<1);
    }
}
