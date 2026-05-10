package models;

import java.util.Objects;

public class SlidingState {
    public Node currentPos;
    public int nextTargetNum;
    public int totalCost;
    public int totalACost;
    public SlidingState parent;
    public String lastMove;

    public SlidingState(Node pos, int nextTarget, int tc, int tac, SlidingState parent, String move) {
        this.currentPos = pos;
        this.nextTargetNum = nextTarget;
        this.totalCost = tc;
        this.totalACost = tac;
        this.parent = parent;
        this.lastMove = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlidingState that = (SlidingState) o;
        return nextTargetNum == that.nextTargetNum && currentPos.x == that.currentPos.x && currentPos.y == that.currentPos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPos.x, currentPos.y, nextTargetNum);
    }
}