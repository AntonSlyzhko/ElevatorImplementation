package com.company.elevator;

import com.company.Human;
import com.company.building.Building;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.company.printer.HumanElevatorMovesPrinter.*;

public class HumanElevator implements Elevator<Human> {
    private static final UnaryOperator<Integer> increment = a -> a + 1;
    private static final UnaryOperator<Integer> decrement = a -> a - 1;
    private static final BiPredicate<Human, Integer> isHumanGoesUp =
            (human, currentFloor) -> human.getWantsToFloor() > currentFloor;
    private static final BiPredicate<Human, Integer> isHumanGoesDown =
            (human, currentFloor) -> human.getWantsToFloor() < currentFloor;
    private final int capacity;
    private final Building building;
    private final Map<Integer, List<Human>> humansWantToFloorMap;
    private int movingToFloor;
    private int size;
    private Direction direction;
    private int currentFloor;
    private boolean isMoving;

    public HumanElevator(int capacity, int floorsCount, Building building) {
        this.capacity = capacity;
        this.building = building;
        humansWantToFloorMap = new HashMap<>();
        for (int i = 1; i <= floorsCount; i++)
            humansWantToFloorMap.put(i, new ArrayList<>());
        direction = Direction.UP;
    }

    @Override
    public List<Human> release() {
        List<Human> leavingHumans = new ArrayList<>(humansWantToFloorMap.get(currentFloor));
        humansWantToFloorMap.get(currentFloor).clear();
        size -= leavingHumans.size();
        return leavingHumans;
    }

    @Override
    public void accept(List<Human> elements) {
        if (elements == null)
            return;
        for (Human human : elements)
            humansWantToFloorMap.get(human.getWantsToFloor()).add(human);
        size += elements.size();
    }

    @Override
    public void stop() {
        isMoving = false;
    }

    @Override
    public void start(){
        isMoving = true;
        while (isMoving){
            move();
            checkForStopCommand();
        }
    }

    private void checkForStopCommand() {
        //TODO: add concurrency. Check the command to stop moving from the Building.
    }

    @Override
    public void move() {
        if(direction == Direction.UP)
            move(increment, isHumanGoesUp);
        else
            move(decrement, isHumanGoesDown);
    }

    public void move(UnaryOperator<Integer> currentFloorOperation, BiPredicate<Human, Integer> humanFilter){
        currentFloor = currentFloorOperation.apply(currentFloor);

        releaseHumans();
        acceptHumans(humanFilter);
        chooseFloor();

        printNewStep();
        printDirection(direction);
        printCurrentFloor(currentFloor);
        printMovingToFloor(movingToFloor);
        printEmptyLine();
        printElevatorState(getAllHumans());
        printEmptyLine();
        printBuildingState(building.getHumansSortedByFloors());
    }

    private List<Human> getAllHumans(){
        return humansWantToFloorMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void chooseFloor(){
        IntStream allRequiredFloors = humansWantToFloorMap.values().stream()
                .flatMap(Collection::stream)
                .mapToInt(Human::getWantsToFloor);
        if(direction == Direction.UP){
            movingToFloor = allRequiredFloors.max().orElseGet(this::chooseNearestFloorWithHumans);
        } else {
            movingToFloor = allRequiredFloors.min().orElseGet(this::chooseNearestFloorWithHumans);
        }

        if((direction == Direction.UP && currentFloor > movingToFloor)
            || (direction == Direction.DOWN && currentFloor < movingToFloor))
            changeDirection();
    }

    private int chooseNearestFloorWithHumans(){
        int[] floorsWithHumans = building.getFloorsWithHumans();
        int distance = Math.abs(floorsWithHumans[0] - currentFloor);
        int idx = 0;
        for(int c = 1; c < floorsWithHumans.length; c++){
            int cdistance = Math.abs(floorsWithHumans[c] - currentFloor);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }
        return floorsWithHumans[idx];
    }

    private void releaseHumans(){
        building.receiveHumans(currentFloor, release());
    }

    private void acceptHumans(BiPredicate<Human, Integer> humanFilter) {
        List<Human> acceptedHumans = null;
        if(size == 0){
            acceptedHumans = getAcceptedHumans(human -> true);
        } else if(size < capacity){
            acceptedHumans = getAcceptedHumans(human -> humanFilter.test(human, currentFloor));
        }
        accept(acceptedHumans);
    }

    private List<Human> getAcceptedHumans(Predicate<Human> humanFilterPredicate){
        return building.putHumans(
                currentFloor,
                humanFilterPredicate,
                capacity - size);
    }

    private void changeDirection(){
        if(direction == Direction.UP)
            direction = Direction.DOWN;
        else
            direction = Direction.UP;
    }
}