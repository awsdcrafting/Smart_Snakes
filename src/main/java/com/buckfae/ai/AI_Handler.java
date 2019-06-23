package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;

public class AI_Handler {

    //----------------Variables to play around with-----------------------------

        int id_of_brain_to_choose = 3;
        /*
            0 -> Algo_Testing_Algo
            1 -> Algo_Always_Move_The_Same_Way
            2 -> Algo_NN_Using_Neuroph
            3 -> Algo_Played_By_Human

            If you want to add a new Algo yourself make sure to add your Algo in the
                switch statement below. This may seem complicated but it allows to
                switch between two Algos just by changing one variable. When creating
                your own Algo make sure to implement Brain, then you will have all
                necessary functions, you just need to fill them with code
        */
    //--------------------------------------------------------------------------

    //All brains will later be instantiated using this one so we don't have to determine which kind of brain we want every time we add a brain to sth
    Brain master_brain;

    public boolean update_this_frame = false;

    public AI_Handler(){

        //Decides what the master_brain will be
        //If you want to add your own Algo add it's id here as well
        switch (id_of_brain_to_choose){
            case 0:
                master_brain = new Algo_Testing_Algo();
                break;
            case 1:
                master_brain = new Algo_Always_Move_The_Same_Way();
                break;
            case 2:
                master_brain = new Algo_NN_Using_Neuroph();
                break;
            case 3:
                master_brain = new Algo_Played_By_Human();
                break;
        }

        //Each game gets their brain
        for(Game game: Processing.games){

            //We create a new instance of the class of our master_brain and add it to a snake
            try {
                game.snake.brain = master_brain.getClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    public void update_population(){

        //Update this frame will always be true if the snakes moved this frame
        if(update_this_frame) {

            //Checks if all Snakes are dead
            boolean all_dead = true;
            //Goes through all snakes and checks if at least one is alive
            for (Game game : Processing.games) {
                if (!game.snake.is_dead) {
                    all_dead = false;
                    break;
                }
            }

            //All snakes are dead
            if (all_dead) {

                //Generates the brains for a new generation and resets the snakes as well
                Processing.games.get(0).snake.brain.generate_brains_for_new_generation();
            }

            //We don't update next frame
            update_this_frame = false;
        }

    }
}
