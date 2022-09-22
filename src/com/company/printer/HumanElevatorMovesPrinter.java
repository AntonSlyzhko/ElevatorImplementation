package com.company.printer;

import com.company.Human;
import com.company.elevator.Direction;

import java.util.List;

public final class HumanElevatorMovesPrinter {
    private static int stepCounter;

    private HumanElevatorMovesPrinter() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void printNewStep(){
        println("============================");
        printField("Step", ++stepCounter);
    }

    public static void printDirection(Direction direction){
        printField("Direction", direction);
    }

    public static void printCurrentFloor(int currentFloor){
        printField("Current floor", currentFloor);
    }

    public static void printMovingToFloor(int movingToFloor){
        printField("Moving to floor", movingToFloor);
    }

    private static void printField(String fieldName, Object fieldValue){
        println(fieldName + " : " + fieldValue.toString());
    }

    public static void printElevatorState(List<Human> humans){
        println(getElevatorState(humans));
    }

    private static String getElevatorState(List<Human> humans){
        StringBuilder sb = new StringBuilder();
        sb.append("Elevator state:\n").append("|");
        sb.append(getHumansAsString(humans));
        sb.append("|");
        return sb.toString();
    }

    public static void printBuildingState(List<List<Human>> humans) {
        println(getBuildingState(humans));
    }

    private static String getBuildingState(List<List<Human>> humans){
        StringBuilder sb = new StringBuilder();
        sb.append("Building state:\n");
        for (int i = humans.size() - 1; i >= 0; i--) {
            sb.append("Floor ")
                    .append(i + 1)
                    .append("\t: ");
            sb.append(getHumansAsString(humans.get(i)));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String getHumansAsString(List<Human> humans){
        StringBuilder sb = new StringBuilder();
        for(Human human : humans)
            sb.append("\t")
                    .append(human.toString())
                    .append("\t");
        return sb.toString();
    }

    public static void printEmptyLine(){
        println("");
    }

    public static void println(String string){
        print(string + "\n");
    }

    public static void print(String string) {
        System.out.print(string);
    }
}
