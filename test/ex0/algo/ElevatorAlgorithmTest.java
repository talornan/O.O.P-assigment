package ex0.algo;
import ex0.Building;
import ex0.CallForElevator;
import ex0.simulator.Builging_A;
import ex0.simulator.Call_A;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ElevatorAlgorithmTest {


    private int runTestSimulation(String path, int caseTest){
        String codeOwner = "23";
        Simulator_A.setCodeOwner(codeOwner);

        int stage = caseTest;  // any case in [0,9].
        String callFile = path == null? null : Paths.get("test/ex0/algo/resources/" + path).toAbsolutePath().toString(); // use the predefined cases [1-9].
        Simulator_A.initData(stage, callFile);  // init the simulator data: {building, calls}.

        ElevatorAlgo ex0_alg_tal = new ElevatorAlgorithm(Simulator_A.getBuilding());    // Shabat Elev with two trick - replace with your code;
        Simulator_A.initAlgo(ex0_alg_tal); // init the algorithm to be used by the simulator

        Simulator_A.runSim(); // run the simulation - should NOT take more than few seconds.

        long time = System.currentTimeMillis();
        String report_name = "out/Ex0_report_case_"+stage+"_"+time+"_ID_test.log";
        Simulator_A.report(report_name); // print the algorithm results in the given case, and save the log to a file.
        //Simulator_A.report(); // if now file  - simple prints just the results.
        Simulator_A.writeAllCalls("out/Ex0_Calls_case_"+stage+"_.csv");
        String s= getLastListFromTheReport(report_name);
        return getUncompletedCalls(s);


    }

    @Test
    public void test0(){

        int uncompleted = runTestSimulation("test.csv", 9);
        assertEquals(uncompleted, 0);
    }
    @Test
    public void test1(){
        int uncompleted = runTestSimulation("test1.csv", 9);
        assertEquals(uncompleted, 0);
    }
    @Test
    public void test2(){
        int uncompleted = runTestSimulation("test2.csv", 9);
        assertEquals(uncompleted, 0);
    }
    @Test
    public void test3(){
        int uncompleted = runTestSimulation("test3.csv", 9);
        assertEquals(uncompleted, 0);
    }
    @Test
    public void test4(){
        int uncompleted = runTestSimulation("test4.csv", 9);
        assertEquals(uncompleted, 1);
    }
    @Test
    public void exerciseTest(){
        int uncompleted0 = runTestSimulation(null, 0);
        assertEquals(uncompleted0, 0);
        int uncompleted1 = runTestSimulation(null, 1);
        assertEquals(uncompleted1, 4);

        int uncompleted2 = runTestSimulation(null, 2);
        assertEquals(uncompleted2, 4);

        int uncompleted3 = runTestSimulation(null, 3);
        assertEquals(uncompleted3, 5);

        int uncompleted4 = runTestSimulation(null, 4);
        assertEquals(uncompleted4, 5);

        int uncompleted5 = runTestSimulation(null, 5);
        assertEquals(uncompleted5, 54);

        int uncompleted6 = runTestSimulation(null, 6);
        assertEquals(uncompleted6, 20);

        int uncompleted7 = runTestSimulation(null, 7);
        assertEquals(uncompleted7, 204);

        int uncompleted8 = runTestSimulation(null, 8);
        assertEquals(uncompleted8, 102);

        int uncompleted9 = runTestSimulation(null, 9);
        assertEquals(uncompleted9, 23);
    }

    private String getLastListFromTheReport(String filePath) {
        try{
            BufferedReader input = new BufferedReader(new FileReader(filePath));
            String last= null;
            String line = null;
            while((line = input.readLine()) != null){
                last = line;

            }
            return last;
        }
        catch(Exception ex){
            return "";
        }
    }
    private int getUncompletedCalls(String line){
        String num = line.split(",")[7];
        return Integer.valueOf(num);
    }

}
