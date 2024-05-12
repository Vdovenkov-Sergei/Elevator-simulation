package Building;

import Tools.Color;

import java.util.Random;
import java.util.concurrent.*;

public class Building implements Callable<String> {
    private final int cntRequests, cntFloors;
    private final double averageDelayRequest, variance;
    private final ControllerElevator controller;
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final Random random = new Random();

    public Building(int floors, int requests, double averageDelay, double velocityElev) {
        cntFloors = floors; cntRequests = requests;
        averageDelayRequest = Math.max(averageDelay, 1.5); variance = averageDelayRequest / 5;
        controller = new ControllerElevator(velocityElev);
    }

    @Override
    public String call() throws ExecutionException, InterruptedException {
        Thread.sleep(500);
        System.out.println(Color.BLUE + "> 'Building' object connected: ✔️");
        Future<String> resultController = service.submit(controller);
        Thread.sleep(5000);
        int curRequest = 0;
        while (curRequest != cntRequests) {
            delayNextRequest();
            controller.addNewRequest(createNewRequest());
            curRequest++;
        }
        controller.finish();
        Thread.sleep(500);
        System.out.format(Color.BLUE + "> 'ControllerElevator' object disabled: %s\n", resultController.get());
        Thread.sleep(500);
        service.shutdownNow();
        return "✔️";
    }

    private void delayNextRequest() throws InterruptedException {
        int delay = (int)((random.nextDouble(-variance, variance) + averageDelayRequest) * 1000);
        Thread.sleep(delay);
    }

    private Request createNewRequest() {
        int from = random.nextInt(1, cntFloors);
        int to = random.nextInt(1, cntFloors);
        while (from == to) {
            to = random.nextInt(1, cntFloors);
        }
        Directions dir = from > to ? Directions.DOWN : Directions.UP;
        Request request = new Request(from, to, dir);

        String infoBuilding = Color.ORANGE + "[\uD83C\uDFE4 Building]" + Color.RESET;
        String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, from);
        String infoBuild = String.format(Color.YELLOW + "[----]" + Color.RESET);
        String infoAction = String.format(Color.CYAN + "New request: To: %d, Dir: %s \uD83D\uDC48" + Color.RESET, to, dir.name());

        System.out.format("%-34s %-23s %-19s %60s\n", infoBuilding, infoFloor, infoBuild, infoAction);
        return request;
    }
}
