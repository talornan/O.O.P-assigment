package ex0.algo;

import ex0.CallForElevator;

/**
 *  Simple Wrapper to call method, just to know what is the current target floor
 */
public class ElevatorDestination {
    private CallForElevator call;

    public ElevatorDestination(CallForElevator call) {

        this.call = call;
    }


    public int getTargetFloor(){
        int floor = call.getState() ==CallForElevator.GOIND2DEST?  call.getDest() : call.getSrc();;
        return floor;
    }

    public CallForElevator getCall() {
        return call;
    }

    public void setCall(CallForElevator call) {
        this.call = call;
    }

}
