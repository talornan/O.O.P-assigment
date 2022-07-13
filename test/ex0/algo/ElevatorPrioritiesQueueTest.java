package ex0.algo;

import ex0.CallForElevator;
import ex0.simulator.Call_A;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

 class ElevatorPrioritiesQueueTest {

    @Test
    public  void TestQueue(){
        ElevatorPrioritiesQueue queue = new ElevatorPrioritiesQueue();
        assertEquals(queue.size(), 0);
        CallForElevator call1 = new Call_A(1,2,3);
        CallForElevator call2 = new Call_A(4,5,6);


        queue.addCall(call1);
        assertEquals(queue.size(), 1);
        queue.addCallToHead(call2);
        assertEquals(queue.peek().getSrc() , 5);
        queue.poll();
        assertEquals(queue.size(), 1);
        assertEquals(queue.get(0).getSrc(), 2);





    }

}
