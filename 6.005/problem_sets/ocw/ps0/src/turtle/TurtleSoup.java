/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package turtle;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class TurtleSoup {

    private static final int DOOR_WIDTH = 4;
    private static final int MAP_SPACING = 3;

    /**
     * Draw a square.
     * 
     * @param turtle the turtle context
     * @param sideLength length of each side
     */
    public static void drawSquare(Turtle turtle, int sideLength) {
        for(int i = 0; i < 4 ; i++) {
            turtle.forward(sideLength);
            turtle.turn(90);
        }
    }

    /**
     * Determine inside angles of a regular polygon.
     * 
     * There is a simple formula for calculating the inside angles of a polygon;
     * you should derive it and use it here.
     * 
     * @param sides number of sides, where sides must be > 2
     * @return angle in degrees, where 0 <= angle < 360
     */
    static double calculateRegularPolygonAngle(int sides) {
        return (double) (180 * (sides - 2)) / sides;
    }

    /**
     * Determine number of sides given the size of interior angles of a regular polygon.
     * 
     * There is a simple formula for this; you should derive it and use it here.
     * Make sure you *properly round* the answer before you return it (see java.lang.Math).
     * HINT: it is easier if you think about the exterior angles.
     * 
     * @param angle size of interior angles in degrees, where 0 < angle < 180
     * @return the integer number of sides
     */
    static int calculatePolygonSidesFromAngle(double angle) {
        return (int) Math.round(360 / (180 - angle));
    }

    /**
     * Given the number of sides, draw a regular polygon.
     * 
     * (0,0) is the lower-left corner of the polygon; use only right-hand turns to draw.
     * 
     * @param turtle the turtle context
     * @param sides number of sides of the polygon to draw
     * @param sideLength length of each side
     */
    public static void drawRegularPolygon(Turtle turtle, int sides, int sideLength) {
        final double angle = 180 - calculateRegularPolygonAngle(sides);

        for (int i = 0; i < sides; i++){
            turtle.forward(sideLength);
            turtle.turn(angle);
        }
    }

    /**
     * Given the current direction, current location, and a target location, calculate the heading
     * towards the target point.
     * 
     * The return value is the angle input to turn() that would point the turtle in the direction of
     * the target point (targetX,targetY), given that the turtle is already at the point
     * (currentX,currentY) and is facing at angle currentHeading. The angle must be expressed in
     * degrees, where 0 <= angle < 360. 
     *
     * HINT: look at http://en.wikipedia.org/wiki/Atan2 and Java's math libraries
     * 
     * @param currentHeading current direction as clockwise from north
     * @param currentX current location x-coordinate
     * @param currentY current location y-coordinate
     * @param targetX target point x-coordinate
     * @param targetY target point y-coordinate
     * @return adjustment to heading (right turn amount) to get to target point,
     *         must be 0 <= angle < 360
     */
    static double calculateHeadingToPoint(double currentHeading, int currentX, int currentY,
                                                 int targetX, int targetY) {
        // First, we'll find the difference vector between our two points.
        final int differenceX = targetX - currentX;
        final int differenceY = targetY - currentY;

        // Then we'll get the heading we'd need to reach the difference point from the
        // origin.
        final double difference_angle = 90 - Math.toDegrees(
                Math.atan2(differenceY, differenceX));

        // This is the actual turn we need to take, but it may be negative (i.e., a left
        // turn) because of Java's modulus behavior. Basically, the answer to x % y always
        // takes the sign of x.
        double turn_angle = (difference_angle - currentHeading) % 360;

        // Make sure we provide a right (clockwise) turn.
        if (turn_angle < 0) {
            turn_angle = 360 + turn_angle;
        }
        return turn_angle;
    }

    /**
     * Given a sequence of points, calculate the heading adjustments needed to get from each point
     * to the next.
     * 
     * Assumes that the turtle starts at the first point given, facing up (i.e. 0 degrees).
     * For each subsequent point, assumes that the turtle is still facing in the direction it was
     * facing when it moved to the previous point.
     * You should use calculateHeadingToPoint() to implement this function.
     * 
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of heading adjustments between points, of size 0 if (# of points) == 0,
     *         otherwise of size (# of points) - 1
     */
    static List<Double> calculateHeadings(List<Integer> xCoords, List<Integer> yCoords) {
        // We'll assert this, since our for loop depends on it.
        assert xCoords.size() == yCoords.size() : "Lists of points must be the same size.";

        final List<Double> turns = new ArrayList<>();
        // The turtle starts facing up (0 degrees).
        double current_heading = 0.0;

        // Compare pairs of points, getting the turn angle needed to travel from the
        // current point to the next point.
        for (int i = 0; i < xCoords.size() - 1; i++) {
            int currentX = xCoords.get(i);
            int currentY = yCoords.get(i);
            int nextX = xCoords.get(i+1);
            int nextY = yCoords.get(i+1);

            double turn_angle = calculateHeadingToPoint(
                    current_heading,
                    currentX, currentY,
                    nextX, nextY);
            turns.add(turn_angle);
            // The turtle ends up facing the direction it turned to get to the next point.
            current_heading = turn_angle;
        }

        return turns;

    }

    private static void drawFeature(
            Turtle turtle,
            int length,
            int width,
            boolean draw_door,
            PenColor previous_color) {

        for (int i=0; i < 2; i++) {
            turtle.forward(length);
            turtle.turn(90);

            turtle.forward(width / 2);
            if (draw_door) {
                drawDoor(turtle, previous_color);
            }
            turtle.forward(width / 2);
            turtle.turn(90);
        }
        // Move forward a bit to avoid unsightly overlap.
        turtle.forward(length + MAP_SPACING);
    }

    private static void drawDoor(Turtle turtle, PenColor previous_color) {
        turtle.color(PenColor.RED);

        // Draw a door symbol (two parallel vertical lines).
        for (int i=0; i < 2; i++){
            turtle.turn(90);
            turtle.forward(DOOR_WIDTH);
            turtle.turn(180);
            turtle.forward(DOOR_WIDTH * 2);
            turtle.turn(180);
            turtle.forward(DOOR_WIDTH);
            turtle.turn(270);
            turtle.forward(DOOR_WIDTH);
        }
        // Return turtle to where we found it.
        turtle.turn(180);
        turtle.forward(DOOR_WIDTH);
        turtle.turn(180);

        turtle.color(previous_color);
    }

    private static void drawMap(Turtle turtle, int rooms){
        final List<Integer> room_dimensions = Arrays.asList(11, 13, 29, 31);
        final List<Integer> corridor_lengths = Arrays.asList(40, 65);
        final int CORRIDOR_WIDTH = 10;
        final List<Integer> CARDINALS = Arrays.asList(0, 90, 270);

        final Random rand = new Random();

        for (int i=0; i < rooms; i++) {
            // Generate a randomly sized corridor and attach a randomly sized room to it.
            int room_length = room_dimensions.get(
                    rand.nextInt(room_dimensions.size()));
            int room_width = room_dimensions.get(
                    rand.nextInt(room_dimensions.size()));
            int corridor_length = corridor_lengths.get(
                    rand.nextInt(corridor_lengths.size()));

            // Draw corridor.
            turtle.color(PenColor.GRAY);
            drawFeature(
                    turtle,
                    corridor_length,
                    CORRIDOR_WIDTH,
                    true,
                    PenColor.GRAY);

            // Draw a randomly sized room, then make a random turn.
            turtle.color(PenColor.BLACK);
            drawFeature(
                    turtle,
                    room_length,
                    room_width,
                    false,
                    PenColor.BLACK);
            turtle.turn(CARDINALS.get(rand.nextInt(CARDINALS.size())));
        }


    }

    /**
     * Draw your personal, custom art.
     * 
     * Many interesting images can be drawn using the simple implementation of a turtle.  For this
     * function, draw something interesting; the complexity can be as little or as much as you want.
     * 
     * @param turtle the turtle context
     */
    private static void drawPersonalArt(Turtle turtle) {
        drawMap(turtle, 75);
    }

    /**
     * Main method.
     * 
     * This is the method that runs when you run "java TurtleSoup".
     * 
     * @param args unused
     */
    public static void main(String args[]) {
        DrawableTurtle turtle = new DrawableTurtle();

//        drawSquare(turtle, 40);
//        drawRegularPolygon(turtle, 6, 40);
        drawPersonalArt(turtle);

        // draw the window
        turtle.draw();
    }

}
