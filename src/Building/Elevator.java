package Building;

import Tools.Color;

import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Elevator implements Callable<String> {
    private final int id;
    private final String colorElev;
    private final double velocityElev;
    private final ConcurrentLinkedQueue<Request> otherRequests;
    private final ArrayDeque<Integer> passengers;
    private volatile int floor;
    private volatile Directions dir;
    private volatile Request curRequest;
    private boolean statusRequest;

    public Elevator(int curId, double velocity, String  color, ConcurrentLinkedQueue<Request> queue) {
        id = curId; velocityElev = velocity; otherRequests = queue;
        floor = 1; dir = Directions.STOP; curRequest = null; statusRequest = false;
        passengers = new ArrayDeque<>(); colorElev = color;
    }

    @Override
    public String call() throws InterruptedException {
        Thread.sleep((int)(velocityElev * 500));
        moveTo(curRequest.getFromFloor());

        Thread.sleep((int)(velocityElev * 500));
        String infoElev = String.format(colorElev + "[⚙️ Elevator %d]" + Color.RESET, id);
        String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, floor);
        String infoDir = Color.YELLOW + "[STOP]" + Color.RESET;
        String infoAction = String.format(Color.CYAN + "Picked up passenger, takes him to %d floor ➡️" +
                Color.RESET, curRequest.getToFloor());
        System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoAction);
        statusRequest = true; passengers.add(curRequest.getToFloor());
        dir = curRequest.getToFloor() > floor ? Directions.UP : Directions.DOWN;

        Thread.sleep((int)(velocityElev * 500));
        moveTo(curRequest.getToFloor());

        Thread.sleep((int)(velocityElev * 500));
        infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, floor);
        infoAction = Color.CYAN + "No passengers. Elevator stopped moving \uD83D\uDED1" + Color.RESET;
        System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoAction);
        curRequest = null; statusRequest = false; dir = Directions.STOP;
        return "✔️";
    }

    private void moveTo(int finalFloor) throws InterruptedException {
        String infoElev = String.format(colorElev + "[⚙️ Elevator %d]" + Color.RESET, id);
        while (true) {
            if (!statusRequest && floor == finalFloor) return;

            getOutOfElevator();
            getIntoElevator();

            if (statusRequest && passengers.isEmpty()) return;
            String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, floor);
            String infoDir = String.format(Color.YELLOW + "[%s]" + Color.RESET, dir.name());
            String infoPassengers = String.format(Color.RED + "[%s]" + Color.RESET, "\uD83D\uDC64".repeat(passengers.size()));
            System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoPassengers);

            switch (dir) {
                case UP -> setFloor(floor + 1);
                case DOWN -> setFloor(floor - 1);
            }
            Thread.sleep((int)(velocityElev * 1000));
        }
    }

    private void getOutOfElevator() throws InterruptedException {
        String infoElev = String.format(colorElev + "[⚙️ Elevator %d]" + Color.RESET, id);
        String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, floor);
        String infoDir = Color.YELLOW + "[STOP]" + Color.RESET;

        var iterator = passengers.iterator();
        while (iterator.hasNext()) {
            Integer floorOut = iterator.next();
            if (floorOut == floor) {
                iterator.remove();
                Thread.sleep((int)(velocityElev * 500));
                String infoAction = String.format(Color.CYAN + "Released the passenger on %d floor ⬅️" +
                        Color.RESET, floorOut);
                System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoAction);
            }
        }
    }

    private void getIntoElevator() throws InterruptedException {
        String infoElev = String.format(colorElev + "[⚙️ Elevator %d]" + Color.RESET, id);
        String infoFloor = String.format(Color.GREEN + "[Floor %d]" + Color.RESET, floor);
        String infoDir = Color.YELLOW + "[STOP]" + Color.RESET;

        synchronized (otherRequests) {
            var iterator = otherRequests.iterator();
            while (iterator.hasNext()) {
                Request request = iterator.next();
                if (dir == request.getDir() && curRequest.getDir() == request.getDir() && floor == request.getFromFloor()) {
                    Thread.sleep((int) (velocityElev * 500));
                    iterator.remove();
                    passengers.add(request.getToFloor());
                    String infoAction = String.format(Color.CYAN + "Picked up passenger, takes him to %d floor ➡️" +
                            Color.RESET, request.getToFloor());
                    System.out.format("%-34s %-23s %-19s %60s\n", infoElev, infoFloor, infoDir, infoAction);
                }
            }
        }
    }

    public String getColor() {
        return  colorElev;
    }

    public int getId() {
        return id;
    }

    private void setFloor(int newFloor) {
        floor = newFloor;
    }

    public int getFloor() {
        return floor;
    }

    public void setDir(Directions newDir) {
        dir = newDir;
    }

    public Directions getDir() {
        return dir;
    }

    public void setCurRequest(Request request) {
        curRequest = request;
    }
}
