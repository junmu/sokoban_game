package app.sokoban.play.point;

public class Position extends Point {
    public int x;
    public int y;

    public Position(int x, int y) {
        super(x, y);
    }

    public Position(Point point) {
        super(point.getX(), point.getY());
        this.x = point.getX();
        this.y = point.getY();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Point point) {
        return point != null && x == point.getX() && y == point.getY();
    }
}
