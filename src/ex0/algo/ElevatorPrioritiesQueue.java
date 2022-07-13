package ex0.algo;

import ex0.CallForElevator;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper to list, support adding element to tail and the head
 */
public class ElevatorPrioritiesQueue {

    private List<CallForElevator> priorities;

    public ElevatorPrioritiesQueue(){
        this.priorities = new LinkedList<>();
    }
    public void addCall(CallForElevator call){
        priorities.add(call);
    }
    public void addCallToHead(CallForElevator call){
        priorities.add(0,call);
    }
    public CallForElevator peek(){
        return priorities.get(0);
    }
    public CallForElevator poll(){
        return priorities.remove(0);
    }
    public boolean isEmpty(){
        return priorities.isEmpty();
    }
    public int size(){
        return this.priorities.size();
    }
    public CallForElevator get(int i){
        return priorities.get(i);
    }
    public CallForElevator set(int i, CallForElevator call){
        return priorities.set(i, call);
    }

}
