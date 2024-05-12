import Building.Building;
import Tools.Color;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        String greeting = String.format("Welcome to " + Color.YELLOW + "'Elevator Simulation'" + Color.RED + "!");
        System.out.format(Color.RED + "-".repeat(31) +  " %43s " + "-".repeat(31), greeting);
        System.out.println(Color.GREEN + "\nPlease, configure settings below:\n");

        Scanner scan = new Scanner(System.in);
        System.out.print(Color.BLUE + "> Number of floors in building :> ");
        int maxFloors = scan.nextInt();
        System.out.print("> Number of requests for simulation :> ");
        int cntRequests = scan.nextInt();
        if (cntRequests == 0) { scan.close(); return; }

        System.out.print("> Average delay of request generation (1 request per <> sec) :> ");
        double freqRequest = scan.nextDouble();
        System.out.print("> Velocity of elevators (1 floor per <> sec) :> ");
        double velocityElev = scan.nextDouble();
        System.out.println(Color.RESET);

        try {
            Building building = new Building(maxFloors, cntRequests, freqRequest, velocityElev);
            ExecutorService execService = Executors.newSingleThreadExecutor();
            Future<String> result = execService.submit(building);
            System.out.format(Color.BLUE + "> 'Building' object disabled: %s", result.get());
            execService.shutdownNow(); scan.close();
        } catch (InterruptedException | ExecutionException err) {
            System.out.println(Color.RED + "Error during simulation: " + err.getMessage());
        }
    }
}