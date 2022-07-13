package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.List;

/**
 * Algorithem for smart elevator.
 * implement using two ElevatorPrioritiesQueue, one for source and one for destination.
 * Each time we are getting elevators calls we tries to assign the elevator by the following rules:
 * 1.In case the floor is "on the way", meaning single stop, choose it. if more then one elevator is matches, choose the one with the smallest queue.
 * 2.if the first step didn't work, calcualte the time, it will take for each elevator, add the queue size, and give different for each step.
 *
 */
public class ElevatorAlgorithm implements ElevatorAlgo {

    private Building _building;
    //enters queue
    private List<ElevatorPrioritiesQueue> elevatorFutureDestinationEnter;
    //exit  queue
    private List<ElevatorPrioritiesQueue> elevatorFutureDestinationExit;
    //destination of the file
    private ElevatorDestination[] elevatorsCurrentDestination;


    /**
     * init data structure
     * @param b
     */
    public ElevatorAlgorithm(Building b){
        this._building = b;
        this.elevatorsCurrentDestination = new ElevatorDestination[ _building.numberOfElevetors()];
        this.elevatorFutureDestinationEnter = new ArrayList<>();
        this.elevatorFutureDestinationExit = new ArrayList<>();
        for(int i=0; i< _building.numberOfElevetors(); i++){
            elevatorFutureDestinationEnter.add(new ElevatorPrioritiesQueue());
            elevatorFutureDestinationExit.add(new ElevatorPrioritiesQueue());
        }
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "Tal and Sonia Algorithm";
    }

    /**
     * throws exception in case value are not valid.
     * @param c
     * @throws RuntimeException
     */
    private void isValidCall(CallForElevator c) throws RuntimeException {
        if(c.getDest() < _building.minFloor() && c.getDest() > _building.maxFloor()){
            throw new RuntimeException("Destnation value in not range");
        }
        if(c.getSrc() < _building.minFloor() && c.getSrc() > _building.maxFloor()){
            throw new RuntimeException("Source Value is not in range");
        }
    }

    /**
     * The second heuristic.
     * Get the full time to reach source and destination,
     * and queue size and reduce point(less is better since we choose the elevator with the smallest amount of point)
     *
     * @param c
     * @param elevator
     * @return
     */
    private double calcTotalTimeForElevator(CallForElevator c, Elevator elevator, int elevatorNum) {
        double timeToReachSrc = Math.abs(elevator.getPos() - c.getSrc()) * 1.0 / elevator.getSpeed() ;
        double timeToReachDst = Math.abs(elevator.getPos() - c.getDest()) * 1.0 / elevator.getSpeed();
        double openAndClosesDoors = elevator.getTimeForClose() + elevator.getTimeForOpen();
        double time = (timeToReachSrc) + timeToReachDst ;
        boolean goingDown = elevator.getState() == Elevator.DOWN && c.getSrc() >= c.getDest();
        boolean goingUp = elevator.getState() == Elevator.UP && c.getSrc() <= c.getDest();
        boolean idle = elevator.getState() == Elevator.LEVEL;
        if(goingDown || goingUp || idle ){
            time -=5;
        }
        int queue=  this.elevatorFutureDestinationEnter.get(elevatorNum).size();
        queue*= 2.0 ;
        queue += elevatorFutureDestinationExit.get(elevatorNum).size()*0.2;
        return( time*0.7 + openAndClosesDoors   + queue) / elevator.getSpeed();
    }


    /**
     * return elevator with the least amount of points.
     * @param validElevator
     * @return
     */
    private int selectRecommendElevator(List<ElevatorTimeEstimation> validElevator){
        int bestElevator = -1;
        double minPoint = Double.MAX_VALUE;
        for(ElevatorTimeEstimation el : validElevator){

            double points = el.getFullTime() ;
            if(points < minPoint){
                minPoint = points;
                bestElevator = el.getElevator();
            }
        }
        return bestElevator;
    }



    @Override
    public int allocateAnElevator(CallForElevator c)  {
        isValidCall(c);

        List<ElevatorTimeEstimation> validElevator = new ArrayList<>();

        int elevatorNum = chooseElevator(c);
        if(elevatorNum != -1){
            return elevatorNum;
        }
        //scan for the best elevator
        for(int i =0; i< _building.numberOfElevetors(); i++){
            Elevator elevator = _building.getElevetor(i);

                ElevatorTimeEstimation elvTimeEstimation = new ElevatorTimeEstimation(i, calcTotalTimeForElevator(c,elevator, i)
                        ,elevator.getState() == Elevator.LEVEL);
                validElevator.add(elvTimeEstimation);
            }

        int elevator = selectRecommendElevator(validElevator);

        this.elevatorFutureDestinationEnter.get(elevator).addCall(c);

        return elevator;
    }

    /**
     * add Call that was in the head back to head, may happend in case we wanted to stop in the middle of the way in different floor
     * @param elev
     */
    private void addOldToHeadOfQueue(int elev){
        CallForElevator oldCall = elevatorsCurrentDestination[elev].getCall();
        if(elevatorsCurrentDestination[elev].getCall().getState() == CallForElevator.GOIND2DEST){
            elevatorFutureDestinationExit.get(elev).addCallToHead(oldCall);
        }
        else {
            elevatorFutureDestinationEnter.get(elev).addCallToHead(oldCall);
        }

    }
    private void changeElevatorDestination(CallForElevator call, int i){
        Elevator elv = getBuilding().getElevetor(i);

        CallForElevator currTarget=  elevatorsCurrentDestination[i].getCall();
        elv.stop(call.getSrc());
        elevatorFutureDestinationExit.get(i).addCall(call);
        elevatorsCurrentDestination[i] = new ElevatorDestination(call);
        if(currTarget.getState() == CallForElevator.GOIND2DEST){
            elevatorFutureDestinationExit.get(i).addCall(currTarget);
        }
        else if(currTarget.getState() == CallForElevator.GOING2SRC){
            elevatorFutureDestinationEnter.get(i).addCallToHead(currTarget);
        }
    }

    /**
     * choose the elevator that is already on the way add the call to the priorities in case in on
     * the way of the elevator and consider the queue size
     * @param call
     * @return
     */
    private int chooseElevator(CallForElevator call){
        int choosedElevator = -1;
        double minPoint = Integer.MAX_VALUE;
       for(int i=0; i< _building.numberOfElevetors(); i++){
           Elevator elv = getBuilding().getElevetor(i);
            boolean elevatorGoingUpAndInTheMiddle = elv.getState() == Elevator.UP &&
                    elv.getPos() <= call.getSrc() && call.getSrc() <= elevatorsCurrentDestination[i].getTargetFloor();
           boolean elevatorGoingDownAndInTheMiddle = elv.getState() == Elevator.DOWN &&
                   elv.getPos() >= call.getSrc() && call.getSrc() >= elevatorsCurrentDestination[i].getTargetFloor();
           if(elevatorGoingUpAndInTheMiddle || elevatorGoingDownAndInTheMiddle){
               double queue = 2*elevatorFutureDestinationEnter.get(i).size() + elevatorFutureDestinationExit.get(i).size();
               if(queue < minPoint){
                   minPoint = queue;
                   choosedElevator = i;
               }


           }
       }
       if(choosedElevator != -1){
           changeElevatorDestination(call,choosedElevator);

       }
        return choosedElevator;
    }


    @Override
    public void cmdElevator(int elev) {



        Elevator elevator = _building.getElevetor(elev);

        //remove destination we already visited
        while (!this.elevatorFutureDestinationExit.get(elev).isEmpty() &&
                this.elevatorFutureDestinationExit.get(elev).peek().getState() == CallForElevator.DONE){
            this.elevatorFutureDestinationExit.get(elev).poll();
        }
        //remove sources we already visited

        while (!this.elevatorFutureDestinationEnter.get(elev).isEmpty() &&
                (this.elevatorFutureDestinationEnter.get(elev).peek().getState() == CallForElevator.GOIND2DEST  || this.elevatorFutureDestinationEnter.get(elev).peek().getState() == CallForElevator.DONE) ){
            CallForElevator call = this.elevatorFutureDestinationEnter.get(elev).poll();
            if(call.getState() == CallForElevator.GOIND2DEST){
                this.elevatorFutureDestinationExit.get(elev).addCall(call);

            }
        }

        //get the earliest call user that should get out of the elevator
        if(!this.elevatorFutureDestinationExit.get(elev).isEmpty() &&
                this.elevatorFutureDestinationExit.get(elev).peek().getState() == CallForElevator.GOIND2DEST){
            if (elevator.getState() == Elevator.LEVEL) {
                CallForElevator call = elevatorFutureDestinationExit.get(elev).poll();
                int dest = call.getDest();
                elevator.goTo(dest);
                elevatorsCurrentDestination[elev] =  new ElevatorDestination(call) ;
                //if the destination is "on the way" lets stop and change destination
            } else if ((elevator.getState() == Elevator.DOWN && elevator.getPos() >
                    elevatorFutureDestinationExit.get(elev).peek().getDest() && elevatorsCurrentDestination[elev].getTargetFloor() < elevatorFutureDestinationExit.get(elev).peek().getDest()) ||
                    (elevator.getState() == Elevator.UP && elevator.getPos() <
                    elevatorFutureDestinationExit.get(elev).peek().getDest() && elevatorsCurrentDestination[elev].getTargetFloor() > elevatorFutureDestinationExit.get(elev).peek().getDest())) {
                CallForElevator call = elevatorFutureDestinationExit.get(elev).poll();
                int dest = call.getDest();
                elevator.stop(dest);
                addOldToHeadOfQueue(elev);
                elevatorsCurrentDestination[elev] = new ElevatorDestination(call);
            }
        }

        //users that starts
         if(!elevatorFutureDestinationEnter.get(elev).isEmpty()) {
             //get the earliest call user that should get out of the elevator

             if (elevator.getState() == Elevator.LEVEL) {
                CallForElevator call = elevatorFutureDestinationEnter.get(elev).poll();
                int dest = call.getSrc();
                elevator.goTo(dest);
                elevatorsCurrentDestination[elev] =  new ElevatorDestination(call);
                elevatorFutureDestinationExit.get(elev).addCall(call);
                 //if the destination is "on the way" lets stop and change source

             } else if (((elevator.getState() == Elevator.DOWN && elevator.getPos() >
                    elevatorFutureDestinationEnter.get(elev).peek().getSrc() &&
                    elevatorsCurrentDestination[elev].getTargetFloor() < elevatorFutureDestinationEnter.get(elev).peek().getSrc()) || (elevator.getState() == Elevator.UP && elevator.getPos() <
                    elevatorFutureDestinationEnter.get(elev).peek().getSrc() && elevatorsCurrentDestination[elev].getTargetFloor() > elevatorFutureDestinationEnter.get(elev).peek().getSrc()) )) {

                CallForElevator call = elevatorFutureDestinationEnter.get(elev).poll();
                int dest = call.getSrc();
                elevator.stop(dest);
                addOldToHeadOfQueue(elev);
                elevatorsCurrentDestination[elev] = new ElevatorDestination(call);
                elevatorFutureDestinationExit.get(elev).addCall(call);

            }

        }



    }

}
