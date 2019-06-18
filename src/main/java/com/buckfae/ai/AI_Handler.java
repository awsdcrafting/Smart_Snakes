package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;

public class AI_Handler {

    //----------------Variables to play around with-----------------------------

        int id_of_brain_to_choose = 1;
        /*
            0 -> Algo_Testing_Algo
            1 -> Algo_Always_Move_The_Same_Way

            If you want to add a new Algo yourself make sure to add your Algo in the
                switch statement below. This may seem complicated but it allows to
                switch between two Algos just by changing one variable. When creating
                your own Algo make sure to implement Brain, then you will have all
                necessary functions, you just need to fill them with code
        */
    //--------------------------------------------------------------------------

    //All brains will later be instantiated using this one so we don't have to determine which kind of brain we want every time we add a brain to sth
    Brain master_brain;

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

    }
}
