package com.buckfae;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.Color.RED;

public class Snake {

    //0 up, 1 right, 2 down, 3 left
    int looking_towards = 2;

    //Initial length of the snake
    int initial_length = 3;

    //The game the snake is attached to
    Game game;

    //All the fields of the snake
    ArrayList<Integer[]> fields;

    boolean make_a_move = true;


    public Snake(Game game) {

        this.game = game;

        this.fields = new ArrayList<Integer[]>();

        //Used to give the snake a random start position
        Random random = new Random();
        int random_x_pos = random.nextInt(game.fields.length - 5);
        int random_y_pos = random.nextInt(game.fields.length - 5);

        //Sets the coordinates of the field
        for (int i = 0; i < initial_length; i++) {

            fields.add(0, new Integer[]{random_x_pos + i, random_y_pos});
            game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;
        }
    }

    public void update_snake() {

        //Right now all the snake does is move forward each frame


        //We are allolwed to move (controlled by Processing)
        if (make_a_move) {


            double input_values[][] = get_input_values();

            //Later the brain gives us the direction
            int direction_to_move = 0;

            //Adds the new field, color will be set later
            fields.add(0, new Integer[]{fields.get(0)[0], fields.get(0)[1] + 1});
            game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;

            //Sets the field back to white, then removes it
            game.fields[fields.get(fields.size() - 1)[0]][fields.get(fields.size() - 1)[1]].is_snake = false;
            fields.remove(fields.size() - 1);

            //Resets frame_counter
            make_a_move = false;
        }


    }

    //Calculates the input values
    public double[][] get_input_values() {

        /*
            Direction * Looking_for = Distance
            Directions:
            0 = Back Left
            1 = Left
            2 = Front Left
            3 = Front
            4 = Front Right
            5 = Right
            6 = Back Right

            Looking_for:
            1 = Wall
            2 = Snake
            3 = Food

        */

        //Initializes inputvalues and sets every value to zero
        double[][] input_values = new double[7][3];
        for(int i = 0; i < input_values.length; i++){
            for(int j = 0; j < input_values[0].length; j++){
                input_values[i][j] = 0;
            }
        }

        int value_to_add_if_not_found = game.fields.length;

        for (int current_direction = 0; current_direction < 7; current_direction++) {

            //Coordinates of the field we will look at later
            int[] looking_modifier = {fields.get(0)[0], fields.get(0)[1]};

            //While we din't find a wall
            while (input_values[current_direction][0] == 0) {

                //Here we determine where we will look next
                switch (current_direction) {
                    case 0: // Back Left
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] -= 1;
                                looking_modifier[1] += 1;
                                break;
                            case 1: // Right
                                looking_modifier[0] -= 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] += 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 3: // Left
                                looking_modifier[0] += 1;
                                looking_modifier[1] += 1;
                                break;
                        }
                        break;
                    case 1: // Left
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] -= 1;
                                break;
                            case 1: // Right
                                looking_modifier[1] -= 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] += 1;
                                break;
                            case 3: // Left
                                looking_modifier[1] += 1;
                                break;
                        }
                        break;
                    case 2: // Ahead Left
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] -= 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 1: // Right
                                looking_modifier[0] += 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] += 1;
                                looking_modifier[1] += 1;
                                break;
                            case 3: // Left
                                looking_modifier[0] -= 1;
                                looking_modifier[1] += 1;
                                break;
                        }
                        break;
                    case 3: // Ahead
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[1] -= 1;
                                break;
                            case 1: // Right
                                looking_modifier[0] += 1;
                                break;
                            case 2: // Down
                                looking_modifier[1] += 1;
                                break;
                            case 3: // Left
                                looking_modifier[0] -= 1;
                                break;
                        }
                        break;
                    case 4: // Ahead Right
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] += 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 1: // Right
                                looking_modifier[0] += 1;
                                looking_modifier[1] += 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] -= 1;
                                looking_modifier[1] += 1;
                                break;
                            case 3: // Left
                                looking_modifier[0] -= 1;
                                looking_modifier[1] -= 1;
                                break;
                        }
                        break;
                    case 5: // Right
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] += 1;
                                break;
                            case 1: // Right
                                looking_modifier[1] += 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] -= 1;
                                break;
                            case 3: // Left
                                looking_modifier[1] -= 1;
                                break;
                        }
                        break;
                    case 6: // Back Right
                        switch (looking_towards){
                            case 0: // Up
                                looking_modifier[0] += 1;
                                looking_modifier[1] += 1;
                                break;
                            case 1: // Right
                                looking_modifier[0] -= 1;
                                looking_modifier[1] += 1;
                                break;
                            case 2: // Down
                                looking_modifier[0] -= 1;
                                looking_modifier[1] -= 1;
                                break;
                            case 3: // Left
                                looking_modifier[0] += 1;
                                looking_modifier[1] -= 1;
                                break;
                        }
                        break;
                }

                //We now know where we will look at and check what's there
                //We are out of bounds
                if (looking_modifier[0] < 0 || looking_modifier[1] < 0 || looking_modifier[0] >= game.fields.length - 1 || looking_modifier[1] >= game.fields.length - 1) {
                    //Sets the distance to the wall
                    input_values[current_direction][0] = Point2D.distance(fields.get(0)[0], fields.get(0)[1], looking_modifier[0], looking_modifier[1]);

                    //If we reached the wall and there is still no snakepart
                    if (input_values[current_direction][1] == 0) {
                        input_values[current_direction][1] = value_to_add_if_not_found;
                    }

                    //If we reached the wall and there is still no food
                    if (input_values[current_direction][2]== 0) {
                        input_values[current_direction][2] = value_to_add_if_not_found;
                    }

                }
                //We are not at the wall
                else {

                    //Field we will look at
                    Field field = game.fields[looking_modifier[0]][looking_modifier[1]];

                    //If the current field is a bodypart and we haven't found a closer one
                    if (field.is_snake && input_values[current_direction][1] == 0) {
                        input_values[current_direction][1] = Point2D.distance(fields.get(0)[0], fields.get(0)[1], looking_modifier[0], looking_modifier[1]);
                    }

                    //If the current field is a snake and we haven't found a closer one
                    if (field.is_food && input_values[current_direction][2] == 0) {
                        input_values[current_direction][2] = Point2D.distance(fields.get(0)[0], fields.get(0)[1], looking_modifier[0], looking_modifier[1]);
                    }
                }


            }
        }

        return input_values;
    }

    public void print_input_values(double[][] input_values){

        System.out.println("Input Values: ");
        for(int direction = 0; direction < input_values.length; direction++){

            for(int looking_for = 0; looking_for < input_values[0].length; looking_for++){

                String obj = "";

                switch (looking_for){
                    case 0:
                        obj = "wall";
                        break;
                    case 1:
                        obj = " snake";
                        break;
                    case 2:
                        obj = "food";
                }

                System.out.println("Distance to " + obj + " in direction " + direction + " is " + input_values[direction][looking_for]);
            }
        }
    }
}
