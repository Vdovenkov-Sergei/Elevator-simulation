package Building;

import Tools.Color;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerElevator implements Callable<String> {
    private final int NUMBER_OF_ELEVATORS = 2;
    private final AtomicBoolean finish;
    private final Elevator elevLeft, elevRight;
    private final ConcurrentLinkedQueue<Request> requestQueue;
    private final ExecutorService serviceElev = Executors.newFixedThreadPool(NUMBER_OF_ELEVATORS);

    public ControllerElevator(double velocityElev) {
        requestQueue = new ConcurrentLinkedQueue<>();
        elevLeft = new Elevator(1, velocityElev, Color.MAGENTA, requestQueue);
        elevRight =  new Elevator(2, velocityElev, Color.PURPLE, requestQueue);
        finish = new AtomicBoolean(false);
    }

    @Override
    public String call() throws InterruptedException, ExecutionException {
        Thread.sleep(500);
        System.out.println(Color.BLUE + "> 'ControllerElevator' object connected: ✔️");
        Thread.sleep(500);
        System.out.format(Color.BLUE + "> 'Elevator [%d]' object connected: ✔️\n", elevLeft.getId());
        Thread.sleep(500);
        System.out.format(Color.BLUE + "> 'Elevator [%d]' object connected: ✔️\n\n", elevRight.getId());

        Future<String> resultLeft = null, resultRight = null;
        while (!finish.get() || !requestQueue.isEmpty()) {
            if (elevLeft.getDir() == Directions.STOP || elevRight.getDir() == Directions.STOP) {

                Request curReq = requestQueue.poll();
                if (curReq == null) continue;

                Elevator bestChoose = getBestElevator(curReq);
                bestChoose.setDir(curReq.getFromFloor() >= bestChoose.getFloor() ? Directions.UP : Directions.DOWN);
                bestChoose.setCurRequest(curReq);
                Thread.sleep(500);

                String colorElev = bestChoose.getColor();
                String infoElev = String.format(colorElev + "[⚙️ Elevator %d]" + Color.RESET, bestChoose.getId());
                String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, bestChoose.getFloor());
                String infoDir = Color.YELLOW + "[STOP]" + Color.RESET;
                String infoAction = String.format(Color.CYAN + "Elevator started moving \uD83D\uDCE2" + Color.RESET);
                System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoAction);

                if (bestChoose.getId() == elevLeft.getId()) resultLeft = serviceElev.submit(bestChoose);
                else resultRight = serviceElev.submit(bestChoose);
            }
        }

        String statusLeft = resultLeft != null ? resultLeft.get() : "✔️";
        String statusRight = resultRight != null ? resultRight.get() : "✔️";
        Thread.sleep(500);
        System.out.format(Color.BLUE + "\n> 'Elevator [%d]' object disabled: %s\n", elevLeft.getId(), statusLeft);
        Thread.sleep(500);
        System.out.format(Color.BLUE + "> 'Elevator [%d]' object disabled: %s\n", elevRight.getId(), statusRight);
        Thread.sleep(500);
        serviceElev.shutdown();
        return "✔️";
    }

    public void finish() {
        finish.set(true);
    }

    public void addNewRequest(Request request) {
        requestQueue.add(request);
    }

    private Elevator getBestElevator(Request request) {
        Elevator best;
        if (elevLeft.getDir() == Directions.STOP && elevRight.getDir() == Directions.STOP) {
            int diffLeft = Math.abs(elevLeft.getFloor() - request.getFromFloor());
            int diffRight = Math.abs(elevRight.getFloor() - request.getFromFloor());
            if (diffLeft <= diffRight) best = elevLeft;
            else best = elevRight;
        }
        else if (elevLeft.getDir() == Directions.STOP) best = elevLeft;
        else best = elevRight;
        return best;
    }
}
