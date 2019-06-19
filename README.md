# Smart_Snakes
Snake Game build in a way that you can test your own algorithms on it.
<br> I am a student and i am working on this project in my free time. 
<br> The project is still work in progress and far from beeing done, so most things are not working yet
## Building the project
If you want to create a runnable jar execute the maven goal package

## How does it all work
1. Main initializes Processing
2. Processing contains a draw() function that is called every frame
3. Processing generates games according to the variable population_size
    * Each game contains a Snake and each Snake has a brain
    * In the first iteration the snakes get their brains from the class
        AI_Handler 
    * The AI_Handler checks every frame if all snakes are dead, if this is the case 
        the generate_brains_for_new_generation() - function from the snake of the game
        at index 0 is called. This function then gives all snakes a new brain (and in case
        of a nn performs all the evolving of the brains)
4. Each time the snakes make a move they generate input_values. 
    * The snake looks in 7 directions
    * In each direction it gets the closest distance to
        * Wall
        * Food (if exists)
        * Other snake bodypart (if exists)
    * Reading the input_values: 
        * input_values[direction][looking for]
        * directions
            * 0 = Back Left
            * 1 = Left
            * 2 = Front Left
            * 3 = Front (Ahead)
            * 4 = Front Right
            * 5 = Right
            * 6 = Right Back 
        * Looking for
            * 0 = Wall
            * 1 = Snake
            * 2 = Food
        * These are relative to the snake, no matter in which direction the snakes head
            is currently looking at, ahead will always track what's ahead.
5. You will be able to add e.g a score mechanic to the snake easily in order to e.g determine
    the fintess of the snake
## How to add an algorithm
1. Create a new class in the package ai and name it Algo_<Name_Of_Your_Algo>
2. Make the algorithm implement the interface brain and make sure to add the methods
3. Provide code for the calculate_next_move and the generate_brains_for_new_generation functions
    * calculate_next_move(double input_values[][]): 
        * Calculates the next move for the snake
        * Should return a value from 0 = Ahead, 1 = Left, 2 = Right
    * generate_brains_for_new_generation()
        * When all snakes are dead this function is called by the class AI_Handler and it
            should add new Brains to all snakes (in case it's a changing nn)
        * Basic example:
            ```javasript
            public void generate_brains_for_new_generation() {
                    
                    //We do this for each game
                    for(Game game: Processing.games){
                    
                        //Generates a new Brain
                        Brain new_brain = new Algo_<Name_Of_Your_Algo>();
                    
                        //Adds the new brain to the snake
                        game.snake.brain = new_brain;
                    }
                }
            ```
            We do not have to reset the snakes position and reset all 
            it's variables as the AI_Handler does that for us once all snakes are dead.
4. Add a case to the switch in the constructor of the AI_Handler 
## Customizing the UI
In the Class Processing you can see a big section saying Variables to play around with
### Increasing the population size
You can change the variable population_size to whatever you want. 
<br>
Note: You can actually display less games than would fit on the screen, but there may be
some bugs
### Size of the main window
By changing the variables main_window_size_x and main_window_size_y you can change the
size of the main window. All UI Elements will change accordingly
<br> Once again: Don't make these values to small
### Space between games
If you want more space between two games just change the variables space_between_games_x
and space_between_games_y
### Spae to the side
If you want some space left / right / up / below all games change the variable spaces_to_side[]
accordingly.
### Default text size
If you want to change the default text size just change the variable default_text_size
