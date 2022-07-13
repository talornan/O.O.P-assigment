package ex0.algo;

import ex0.Elevator;


/**
 * Each Elevator is given number of point, related to match to the given elevator call.
 * number of points is based on our heuristic
 */
public class ElevatorTimeEstimation {

    private int elevator;
    private double fullTime;
    private boolean wasTheElevatorWaite;

    public ElevatorTimeEstimation(int elevator, double fullTime, boolean wasTheElevatorWaite) {
        this.elevator = elevator;
        this.fullTime = fullTime;
        this.wasTheElevatorWaite = wasTheElevatorWaite;
    }

    public int getElevator() {
        return elevator;
    }

    public void setElevator(int elevator) {
        this.elevator = elevator;
    }

    public double getFullTime() {
        return fullTime;
    }

    public void setFullTime(double fullTime) {
        this.fullTime = fullTime;
    }

    public boolean isWasTheElevatorWaite() {
        return wasTheElevatorWaite;
    }

    public void setWasTheElevatorWaite(boolean wasTheElevatorWaite) {
        this.wasTheElevatorWaite = wasTheElevatorWaite;
    }
}
