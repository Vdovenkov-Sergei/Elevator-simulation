package Building;

public class Request {
    private final int fromFloor, toFloor;
    private final Directions dir;

    public Request(int from, int to, Directions dirMove) {
        fromFloor = from; toFloor = to; dir = dirMove;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public Directions getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return String.format("From: %d, To: %d, Direction: %s", fromFloor, toFloor, dir.name());
    }
}
