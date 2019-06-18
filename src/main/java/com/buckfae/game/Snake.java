package com.buckfae.game;

import com.buckfae.ai.Brain;
import com.buckfae.game.Field;
import com.buckfae.game.Game;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Snake {

    //0 up, 1 right, 2 down, 3 left
    int looking_towards = 2;

    //Initial length of the snake
    int initial_length = 3;

    //The game the snake is attached to
    Game game;

    //All the fields of the snake
    ArrayList<Integer[]> fields;

    //The brain of the snake
    public Brain brain;

    boolean make_a_move = false;


    public Snake(Game game) {

        this.game = game;

        this.fields = new ArrayList<Integer[]>();

        //Used to give the snake a random start position
        Random random = new Random();
        int random_x_pos = random.nextInt(game.fields.length - 5) + 2;
        int random_y_pos = random.nextInt(game.fields.length - 5) + 2;

        //Sets the coordinates of the field
        for (int i = 0; i < initial_length; i++) {

            fields.add(0, new Integer[]{random_x_pos, random_y_pos + i});
            game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;
        }
    }

    public void update_snake() {

        //Right now all the snake does is move forward each frame


        //We are allolwed to move (controlled by Processing)
        if (make_a_move) {

            //We will pass these later to our brain to get the direction to move
            double input_values[][] = get_input_values();

            //New coordinates of the new frontfield
            int[] new_front_field_coordinates_modifier = {0, 0};

            //Gets the next direction from it's Brain
            int direction_to_move = brain.calculate_next_move(input_values);

            //Depending on where we have to go next and where we are looking at we determine the coordinates of the new frontfield
            switch(direction_to_move){
                //Looking towards: 0 up, 1 right, 2 down, 3 left
                case 0: //Ahead
                    switch (looking_towards){
                        case 0: //Up
                            new_front_field_coordinates_modifier[1] = -1;
                            looking_towards = 0;
                            break;
                        case 1: //Right
                            new_front_field_coordinates_modifier[0] = +1;
                            looking_towards = 1;
                            break;
                        case 2: //Down
                            new_front_field_coordinates_modifier[1] = +1;
                            looking_towards = 2;
                            break;
                        case 3: //Left
                            new_front_field_coordinates_modifier[0] = -1;
                            looking_towards = 3;
                            break;
                    }
                    break;
                case 1: //Left
                    switch (looking_towards){
                        case 0: //Up
                            new_front_field_coordinates_modifier[0] = -1;
                            looking_towards = 3;
                            break;
                        case 1: //Right
                            new_front_field_coordinates_modifier[1] = -1;
                            looking_towards = 0;
                            break;
                        case 2: //Down
                            new_front_field_coordinates_modifier[0] = +1;
                            looking_towards = 1;
                            break;
                        case 3: //Left
                            new_front_field_coordinates_modifier[1] = +1;
                            looking_towards = 2;
                            break;
                    }
                    break;
                case 2: //Right
                    switch (looking_towards){
                        case 0: //Up
                            new_front_field_coordinates_modifier[0] = +1;
                            looking_towards = 1;
                            break;
                        case 1: //Right
                            new_front_field_coordinates_modifier[1] = +1;
                            looking_towards = 2;
                            break;
                        case 2: //Down
                            new_front_field_coordinates_modifier[0] = -1;
                            looking_towards = 3;
                            break;
                        case 3: //Left
                            new_front_field_coordinates_modifier[1] = -1;
                            looking_towards = 0;
                            break;
                    }
                    break;

            }

            //Adds the new field, color will be set later
            fields.add(0, new Integer[]{fields.get(0)[0] + new_front_field_coordinates_modifier[0], fields.get(0)[1] + new_front_field_coordinates_modifier[1]});
            game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;

            //Sets last field back to white, then removes it
            game.fields[fields.get(fields.size() - 1)[0]][fields.get(fields.size() - 1)[1]].is_snake = false;
            fields.remove(fields.size() - 1);

            //Resets frame_counter
            make_a_move = false;


            //WE are done moving, getting input values to show if shit works
            if(game.game_x == Processing.games.get(0).game_x && game.game_y == Processing.games.get(0).game_y) {
                print_input_values(get_input_values());
            }
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
                if (looking_modifier[0] < 0 || looking_modifier[1] < 0 || looking_modifier[0] > game.fields.length - 1 || looking_modifier[1] > game.fields[0].length - 1) {
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
        System.out.println("LTW " + looking_towards);
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
