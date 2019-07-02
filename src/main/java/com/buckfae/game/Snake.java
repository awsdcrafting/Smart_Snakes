package com.buckfae.game;

import com.buckfae.ai.Brain;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Snake {

    public int score = 300; //Initial score of the snake

    //Kills the Snake after this many steps (Snake should not go on for ever)
    public int die_after_steps; //Set in Constructor as rn we don't have acess to that variable
    public int steps_done = 0;

    //0 up, 1 right, 2 down, 3 left
    int looking_towards = 2;

    //Initial length of the snake
    int initial_length = 3;

    //The game the snake is attached to
    Game game;

    //All the fields of the snake
    public ArrayList<Integer[]> fields;

    //The brain of the snake
    public Brain brain;

    //If this is true the snake will move
    boolean make_a_move = false;

    //Tracks if the snake has died
    public boolean is_dead = false;

    //Tracks what the Snake died to, 0 -> Alive 1 -> Wall 2 -> Self 3 -> Steps
    public int died_to = 0;

    //x and y position of the food
    public int[] food = new int[2];

    //x and y position of the head
    public int[] head = new int[2];

    //Iterations the game waits till it replaces a dead snake with a new one
    public int frames_till_replace = 50;

    //Keeps track of the last fields we visited in order to check if we are just running in circles
    ArrayList<Integer[]> last_fields = new ArrayList<Integer[]>();
    //Tracks the last distance to food, score increases if we got closer and decreses otherwise
    double last_distance_to_food = Double.MAX_VALUE;

    private Random random;
    public long seed;

    public Snake(Game game){
        this(game,0);
    }

    public Snake(Game game,long seed) {
        this.seed = seed;
        random = new Random(seed);
        //Sets the value to the maximum amount of moves
        die_after_steps = game.fields.length * game.fields.length;

        this.game = game;

        this.fields = new ArrayList<Integer[]>();

        //Used to give the snake a random start position
        //Snake starts at a random x and has its head between 2 and the size of the field
        Random random = new Random();
        int random_x_pos = random.nextInt(game.fields.length);
        int random_y_pos = random.nextInt(game.fields.length - 4) + 2;

        //Ensures that we have a clear game at the beginning
        for(Field[] fields: game.fields){
            for(Field field: fields){
                field.is_snake = false;
                field.is_food = false;
                field.isSnakeHead = false;
            }
        }

        //Sets the coordinates of the field
        for (int i = 0; i < initial_length; i++) {

            fields.add(0, new Integer[]{random_x_pos, random_y_pos + i});
            game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;
        }
        head[0] = fields.get(0)[0];
        head[1] = fields.get(0)[1];
        game.fields[head[0]][head[1]].isSnakeHead = true;

        //Adds all fields to the last fields
        last_fields.addAll(fields);

        //Spawns the food
        generate_new_food();
    }

    //Evaluates if the move was good or bad
    public void evauluate_move(){

        /*
            We evaulate the following things:
                1. Did we just die?
                2. Are we closer to the food than we were 3 moves ago
                3. Are we on a tile we were on the last lenght() + 2 moves? -> Prevents from doing circles
        */
        //Tracks what the Snake died to, 0 -> Alive 1 -> Wall 2 -> Self 3 -> Steps
        switch (died_to){
            case 1: //Died to wall
                //core -= 310;
                break;
            case 2: //Died to itself
                //score -= 290;
                break;
            case 3: //Out of steps
                //score -= 320;
                break;
                default: //Nothing happens
                    break;
        }

        //Checks if we did run in a circle
        boolean did_a_circle = false;
        for(Integer[] i : last_fields){
            //We have been on this tile before
            if (fields.get(0)[0].equals(i[0]) && fields.get(0)[1].equals(i[1])){
                did_a_circle = true;
            }
        }

        //If we've gotten closer to the food we get a reward
        double new_distance_to_food = Point2D.distance(fields.get(0)[0], fields.get(0)[1],food[0],food[1]);
        if(new_distance_to_food > last_distance_to_food){
            //score += 5;
        } else {
            //score -= 10;
        }

        last_distance_to_food = new_distance_to_food;

        //Punishment if we went in a circle
        if(did_a_circle){
            //score -= 10;
        } else {
            //score += 5;
        }

        //We now add the new front field to the arraylist
        last_fields.add(0, fields.get(0));

        //If we are on food
        if(fields.get(0)[0].equals(food[0]) && fields.get(0)[1].equals(food[1])){
            score += 100 * fields.size(); //More reward the longer the snake is
            last_fields.clear();
        }

    }

    public void update_snake() {

        //We are allolwed to move (controlled by Processing)
        if (make_a_move) {

            if (!is_dead) {

                //We will pass these later to our brain to get the direction to move
                double input_values[][] = get_input_values();

                //New coordinates of the new frontfield
                int[] new_front_field_coordinates_modifier = {0, 0};

                //Gets the next direction from it's Brain
                int direction_to_move = brain.calculate_next_move(input_values);

                //Depending on where we have to go next and where we are looking at we determine the coordinates of the new frontfield
                switch (direction_to_move) {
                    //Looking towards: 0 up, 1 right, 2 down, 3 left
                    case 0: //Ahead
                        switch (looking_towards) {
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
                        switch (looking_towards) {
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
                        switch (looking_towards) {
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


                //Checks if this field is out of bounds -> Snake hit a wall
                if (fields.get(0)[0] + new_front_field_coordinates_modifier[0] < 0
                        || fields.get(0)[0] + new_front_field_coordinates_modifier[0] > game.fields.length - 1
                        || fields.get(0)[1] + new_front_field_coordinates_modifier[1] < 0
                        || fields.get(0)[1] + new_front_field_coordinates_modifier[1] > game.fields.length - 1) {

                    //We did hit a wall
                    is_dead = true;
                    died_to = 1;
                }
                //We didn't crash into a wall



                //Loops through all fields and checks if two have the same coordinates now
                for (int i = 1; i < fields.size(); i++) {
                    if (fields.get(0)[0] + new_front_field_coordinates_modifier[0] == fields.get(i)[0]
                            && fields.get(0)[1] + new_front_field_coordinates_modifier[1] == fields.get(i)[1]) {

                        //Snake is now dead, we track that we died to a bodypiece
                        is_dead = true;
                        died_to = 2;
                        break;
                    }
                }

                if (steps_done++ >= die_after_steps) {
                    is_dead = true;
                    died_to = 3;
                }

                //Our own move won't kill us
                if (!is_dead) {

                    //update head
                    game.fields[head[0]][head[1]].isSnakeHead = false;
                    head[0] += new_front_field_coordinates_modifier[0];
                    head[1] += new_front_field_coordinates_modifier[1];
                    game.fields[head[0]][head[1]].isSnakeHead = true;

                    //Adds the new field, color will be set later
                    fields.add(0, new Integer[]{fields.get(0)[0] + new_front_field_coordinates_modifier[0], fields.get(0)[1] + new_front_field_coordinates_modifier[1]});
                    game.fields[fields.get(0)[0]][fields.get(0)[1]].is_snake = true;

                    //Adjusts the score based on if the move was good or bad
                    evauluate_move();

                    //Checks if we are on food
                    boolean is_on_food = false;
                    for (Integer[] i : fields) {
                        if (i[0] == food[0] && i[1] == food[1]) {
                            is_on_food = true;
                            //generates new food for the next time
                            generate_new_food();
                            break;
                        }
                    }

                    //If we are not on food (if we are our length increases and we don't need to remove one
                    if (!is_on_food) {
                        //Sets last field back to white, then removes it
                        game.fields[fields.get(fields.size() - 1)[0]][fields.get(fields.size() - 1)[1]].is_snake = false;
                        fields.remove(fields.size() - 1);
                    }

                    //Resets frame_counter
                    make_a_move = false;
                }

            }
        }
        //We are dead -> Waiting to be replaced
        if(is_dead){
            if(--frames_till_replace == 0) {

                //We replace this snake with one alive (if this one is visible)
                if (game.is_currently_shown) {

                    //Looping through all games, searching for one that is alive and not shown
                    for (Game game : Processing.games) {

                        //We found a game that is not shown and the snake is alive
                        if (!game.is_currently_shown && !game.snake.is_dead) {

                            //We swap the games
                            Game.switch_two_games_position(game, this.game);
                            break;
                        }
                    }
                }
            }
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

        int value_to_add_if_not_found = Processing.games.get(0).fields.length;

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

    public void generate_new_food(){


        //Removes the old food from the fields
        game.fields[fields.get(0)[0]][fields.get(0)[1]].is_food = false;

        //Until we spawned food successfull
        while_spawn_food:
        while(true){
            int food_x = random.nextInt(game.fields.length);
            int food_y = random.nextInt(game.fields.length);

            //Checks if the snake currently is on this position
            for(Integer[] i: fields){
                //The coordinates are the same, we start again
                if(i[0] == food_x && i[1] == food_y){
                    continue while_spawn_food;
                }
            }
            //The snake is not on these coordinates -> We have a new food
            game.fields[food_x][food_y].is_food = true;
            food[0] = food_x;
            food[1] = food_y;

            //Resets steps done
            steps_done = 0;

            break;
        }
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
