package com.buckfae.ai;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.NeuralNetworkFactory;

public class Algo_Generic_AI implements Brain {

    //The neural network
    MultiLayerPerceptron neural_network;

    //Ammount of neurons in each layer
    public int input_neurons_count = 21;
    public int[] hidden_layer_neurons_count = {16, 16, 16};
    public int output_layer_neurons_count = 1;


    public Algo_Generic_AI(){

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


        return 0;
    }

    public MultiLayerPerceptron clone_neural_network(){
        return null;
    }

    public void generate_brains_for_new_generation() {

    }
}
