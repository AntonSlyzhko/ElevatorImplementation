package com.company;

import com.company.random.RandomWithExcludeRange;

public class Human {
    private static int idForNew;
    private final RandomWithExcludeRange random;
    private final int id;
    private int wantsToFloor;

    public Human(RandomWithExcludeRange random){
        this.random = random;
        id = idForNew++;
    }

    public int getWantsToFloor() {
        return wantsToFloor;
    }

    public void chooseFloor(int lowest, int highest, int current){
        wantsToFloor = random.nextIntInRangeButExclude(lowest, highest, current);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Human)) return false;
        Human human = (Human) o;
        return id == human.id && wantsToFloor == human.wantsToFloor;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + Integer.hashCode(wantsToFloor);
        return result;
    }

    @Override
    public String toString(){
        return "" + wantsToFloor;
    }
}