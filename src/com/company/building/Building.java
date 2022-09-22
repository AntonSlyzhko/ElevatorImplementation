package com.company.building;

import com.company.Human;
import com.company.elevator.Elevator;
import com.company.elevator.HumanElevator;
import com.company.random.RandomWithExcludeRange;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Building {
    private static final int MIN_FLOORS = 5;
    private static final int MAX_FLOORS = 20;
    private static final int MIN_HUMANS_ON_FLOOR_AT_START = 0;
    private static final int MAX_HUMANS_ON_FLOOR_AT_START = 10;
    private static final int ELEVATOR_CAPACITY = 5;
    private final Map<Integer, List<Human>> humansOnFloorsMap;
    private final Elevator<Human> elevator;
    private final int floorsCount;

    public Building(){
        RandomWithExcludeRange random = new RandomWithExcludeRange();
        floorsCount = random.nextInt(MIN_FLOORS, MAX_FLOORS + 1);
        humansOnFloorsMap = new HashMap<>(floorsCount);
        for (int i = 1; i <= floorsCount; i++) {
            int humansOnFloor = random.nextInt(MIN_HUMANS_ON_FLOOR_AT_START, MAX_HUMANS_ON_FLOOR_AT_START + 1);
            List<Human> humansOnFloorList = new ArrayList<>(humansOnFloor);
            for (int j = 1; j <= humansOnFloor; j++){
                Human human = new Human(random);
                triggerHumanChooseFloor(human, i);
                humansOnFloorList.add(human);
            }
            humansOnFloorsMap.put(i, humansOnFloorList);
        }
        elevator = new HumanElevator(ELEVATOR_CAPACITY, floorsCount, this);
    }

    public void start(){
        elevator.start();
    }

    public List<Human> getAcceptableHumans(int floor, Predicate<Human> humanPredicate){
        return humansOnFloorsMap.get(floor).stream()
                .filter(humanPredicate)
                .collect(Collectors.toList());
    }

    public void receiveHumans(int floor, List<Human> humans){
        for (Human human : humans)
            triggerHumanChooseFloor(human, floor);
        humansOnFloorsMap.get(floor).addAll(humans);
    }

    public List<Human> putHumans(int floor, Predicate<Human> humanPredicate, int elevatorCanAccept){
        List<Human> acceptableHumans = getAcceptableHumans(floor, humanPredicate);
        if(acceptableHumans.size() > elevatorCanAccept)
            acceptableHumans = acceptableHumans.subList(0, elevatorCanAccept);
        humansOnFloorsMap.get(floor).removeAll(acceptableHumans);
        return acceptableHumans;
    }

    public int[] getFloorsWithHumans(){
        return humansOnFloorsMap.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .mapToInt(Map.Entry::getKey)
                .sorted()
                .toArray();
    }

    public List<List<Human>> getHumansSortedByFloors(){
        return humansOnFloorsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void triggerHumanChooseFloor(Human human, int floor){
        human.chooseFloor(1, floorsCount, floor);
    }
}
