package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Weight;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//This is an old version of a recent project, i need to rewrite this
public class Algo_NN_Using_Neuroph implements Brain {

    public MultiLayerPerceptron neural_network;

    //Ammount of neurons in each layer
    public int input_neurons_count = 21;
    public int[] hidden_layer_neurons_count = {24};
    public int output_layer_neurons_count = 3;

    //The generation we are currently at
    public static int generation = 0;

    //The mutation rate in percent
    int mutationRate = 7;

    //By how much will be mutated
    double muationRange = 0.2;

    public Algo_NN_Using_Neuroph(){

        //Will later hold all neurons
        int allNeurons[] = new int[hidden_layer_neurons_count.length + 2];

        //Adds the neurons in the input and output layer
        allNeurons[0] = input_neurons_count;
        allNeurons[hidden_layer_neurons_count.length + 1] = output_layer_neurons_count;

        //Adds all neurons in the hidden layers
        for (int i = 0; i < hidden_layer_neurons_count.length; i++) {
            allNeurons[i + 1] = hidden_layer_neurons_count[i];
        }

        //The network is created
        neural_network = new MultiLayerPerceptron(allNeurons);

        //The network gets randomized weights between 0 and 1
        neural_network.randomizeWeights(-1, 1);
    }

    public int calculate_next_move(double[][] input_values) {


        int temp = 0;
        double[] new_input = new double[input_values.length * input_values[0].length];
        for(int i = 0; i < input_values.length; i++){
            for(int j = 0; j < input_values[0].length; j++){
                new_input[temp++] = Processing.processing.map((float) input_values[i][j],0, Processing.games.get(0).fields.length * Processing.games.get(0).fields.length, 0, 1);

            }
        }

        neural_network.setInput(new_input);

        neural_network.calculate();

        double networkOutput[] = neural_network.getOutput();

        //If the first output is the biggest
        if (networkOutput[0] >= networkOutput[1] && networkOutput[0] >= networkOutput[2]) {
            return 0;
        }
        //if the second output is the biggest
        if (networkOutput[1] >= networkOutput[0] && networkOutput[1] >= networkOutput[2]) {
            return 1;
        }
        //if the third output is the biggest
        if (networkOutput[2] >= networkOutput[0] && networkOutput[2] >= networkOutput[1]) {
            return 2;
        }


        System.out.println("FATAL ERROR, NO VALUE WAS THE BIGGEST");
        return 0;
    }

    public void generate_brains_for_new_generation() {

        ArrayList<Algo_NN_Using_Neuroph> brain_pool = new ArrayList<Algo_NN_Using_Neuroph>();

        //We do this for each game
        for(Game game: Processing.games) {

                //Adds each snake to the pool (at least 1 time)
                for (int i = 0; i < Math.max(game.snake.score, 1); i++) {
                    Algo_NN_Using_Neuroph snake_brain = (Algo_NN_Using_Neuroph) game.snake.brain;


                    brain_pool.add(snake_brain);

            }
        }

        System.out.println("Size of Brainpool " + brain_pool.size());
        int died_to_wall = 0;
        int died__to_self = 0;
        int died_to_steps = 0;
        double avg_score = 0;

        for(int i = 0; i < Processing.games.size(); i++){
            Snake snake = Processing.games.get(i).snake;
            if(snake.died_to == 1){
                died_to_wall++;
            } else if(snake.died_to == 2){
                died__to_self++;
            } else {
                died_to_steps++;
            }

            avg_score += snake.fields.size();
        }

        avg_score /= Processing.games.size();

        System.out.println("Avg length in Generation " + generation + ": " + avg_score);
        System.out.println("Snakes died to wall: " + died_to_wall);
        System.out.println("Snakes died to own body " + died__to_self);
        System.out.println("Snakes died out of steps " + died_to_steps);
        System.out.println();
        System.out.println();

        generation++;

        for(Game game: Processing.games){


            //Resets the Snake
            game.snake = new Snake(game);

            game.snake.brain = do_cross_over(brain_pool);

        }

    }

    public Algo_NN_Using_Neuroph do_cross_over(ArrayList<Algo_NN_Using_Neuroph> brain_pool){

        Algo_NN_Using_Neuroph new_brain = new Algo_NN_Using_Neuroph();

        Random random = new Random();

        MultiLayerPerceptron network1 = brain_pool.get(random.nextInt(brain_pool.size())).neural_network;
        MultiLayerPerceptron network2 = brain_pool.get(random.nextInt(brain_pool.size())).neural_network;

        //Input Layer, Hidden Layers, Output Layer
        int ammount_of_Layers = 2 + hidden_layer_neurons_count.length;

        //The layer and neuron where the cut will be
        int devider_layer = random.nextInt(ammount_of_Layers);
        int devider_neuron = random.nextInt(network1.getLayerAt(devider_layer).getNeuronsCount());

        //Loops through all layers
        for(int layerID = 0; layerID < ammount_of_Layers; layerID++){

            //Loops through all neurons in this layer
            for(int neuronID = 0; neuronID < network1.getLayerAt(layerID).getNeuronsCount(); neuronID++){

                //Holds the weights that will later be set for the new network
                Weight weightsToBeAdded[];

                //We copy the weights
                if(layerID < devider_layer && neuronID < devider_neuron){

                    weightsToBeAdded = network1.getLayerAt(layerID).getNeuronAt(neuronID).getWeights();

                } else {

                    weightsToBeAdded = network2.getLayerAt(layerID).getNeuronAt(neuronID).getWeights();

                }

                //Holds the weights from the current network
                Weight weightsAtCurrentNetwork[] = new_brain.neural_network.getLayerAt(layerID).getNeuronAt(neuronID).getWeights();

                //We now add the weights to our network and mutate it
                for(int weightID = 0; weightID < weightsToBeAdded.length; weightID++){

                    //adding the weight
                    weightsAtCurrentNetwork[weightID].setValue(weightsToBeAdded[weightID].getValue());

                    //mutating the weight
                    if(random.nextInt(100) < mutationRate){
                        //Gets the current value
                        double newValue = weightsAtCurrentNetwork[weightID].getValue();

                        //randomizes the value
                        newValue += (- muationRange + (muationRange - -muationRange) * random.nextDouble());
                        //sets the value
                        weightsAtCurrentNetwork[weightID].setValue(newValue);
                    }
                }
            }
        }
        return new_brain;
    }

}
