package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;
import me.tongfei.progressbar.ProgressBar;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Algo_Generic_AI implements Brain {

    public MultiLayerPerceptron neural_network;

    //The generation we are currently at
    public static int generation = 0;

    static int mutation_rate = 7; //Mutation rate in percent
    static double mutation_range = 0.2; //Range of mutation

    //If this is true, we will generate bigger brains
    public static boolean generate_bigger_brains = false;

    public Algo_Generic_AI(int input_neurons_count, ArrayList<Integer> hidden_layer_neurons_count, int output_neurons_count) {

        //Will hold all the neurons as we have to inittialize the neural_network with an int array
        int[] neurons_count = new int[hidden_layer_neurons_count.size() + 2]; // Storing just all hidden layer neurons would be size, we add input and output neurons -> size + 2

        //Adds the desired amount to the array
        neurons_count[0] = input_neurons_count;
        neurons_count[neurons_count.length - 1] = output_neurons_count;

        //We add the desired amount of hidden layers with their desired neuron count to the array
        for (int hiddenlayer_id = 0; hiddenlayer_id < hidden_layer_neurons_count.size(); hiddenlayer_id++) {
            neurons_count[hiddenlayer_id + 1] = hidden_layer_neurons_count.get(hiddenlayer_id); //[hiddenlayer_id + 1] as the input layer is in front of the hidden layers
        }

        //Initializes the neural network with the desired amount of layers / neurons
        neural_network = new MultiLayerPerceptron(neurons_count);

        //We randomize the weights of the network between 0 and 1
        neural_network.randomizeWeights(-1, 1);
    }


    @Override
    public int calculate_next_move(double[][] input_values) {

        int temp = 0;
        double[] new_input = new double[input_values.length * input_values[0].length];
        for (int i = 0; i < input_values.length; i++) {
            for (int j = 0; j < input_values[0].length; j++) {
                new_input[temp++] = Processing.processing.map((float) input_values[i][j], 0, Processing.games.get(0).fields.length, 0, 1);
                //new_input[temp++] = input_values[i][j];
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

    @Override
    public void generate_brains_for_new_generation() {

        //Stores all brains for the new generation
        ArrayList<Algo_Generic_AI> brain_pool = new ArrayList<Algo_Generic_AI>();

        //We have to make the brains bigger
        if (generate_bigger_brains) {

            int amount_of_top_brains_to_get = 20;

            //In case that our population is really small
            if (amount_of_top_brains_to_get > Processing.population_size) {
                amount_of_top_brains_to_get = Processing.population_size;
            }

            Snake[] best_snakes = new Snake[amount_of_top_brains_to_get];
            int next_free_space = 0; //Used to know where the next free space is

            //We get the best scores
            for (Game game : Processing.games) {

                int score = game.snake.score;

                //We still have some empty spots
                if (next_free_space < amount_of_top_brains_to_get) {

                    //We add the brain into the first empty spot
                    best_snakes[next_free_space++] = game.snake;
                }

                //We now have to check if the score of the current game is higher than any of the ones in our array
                else {
                    //We get the id of the Game with the lowest score

                    //Stores the values of the worst snake
                    int lowest_score = Integer.MAX_VALUE;
                    int lowest_score_id = -4;

                    for (int id = 0; id < amount_of_top_brains_to_get; id++) {

                        //If the current snake has a lower score than any snake we looked at so far
                        if (best_snakes[id].score < lowest_score) {
                            lowest_score = best_snakes[id].score;
                            lowest_score_id = id;
                        }
                    }

                    //Our snake is better than one of our current best ones -> We remove the worst one and insert our current one
                    if (lowest_score_id != -4 && score > lowest_score) {
                        best_snakes[lowest_score_id] = game.snake; //We store the snake
                    }
                }
            }

            //We now have the best Snakes

            //The amount of hidden layers for the new snakes
            ArrayList<Integer> hidden_layer_neuron_count = new ArrayList<Integer>(Arrays.asList(24, 24));

            //Initializes a new Progressbar
            try (ProgressBar pb = new ProgressBar("Generating new Snakes", amount_of_top_brains_to_get)) {

            for (int i = 0; i < best_snakes.length; i++) {

                    brain_pool.add(
                            generate_new_neural_net_with_other_layers_neurons(
                                    (Algo_Generic_AI) best_snakes[i].brain,
                                    hidden_layer_neuron_count));

                    pb.step(); //Adds one step to the progressbar
                }
            }

            System.out.println();

            //We reset the boolean
            generate_bigger_brains = false;

        }
        //Size of the brains remains the same
        else {

            //We add the brain of each Snake to the pool
            for (Game game : Processing.games) {

                //Adds each snake to the pool (at least 1 time)
                for (int i = 0; i < Math.max(game.snake.score, 1); i++) {
                    Algo_Generic_AI snake_brain = (Algo_Generic_AI) game.snake.brain;

                    brain_pool.add(snake_brain);

                }
            }
        }

        System.out.println("Size of Brainpool " + brain_pool.size());
        int died_to_wall = 0;
        int died__to_self = 0;
        int died_to_steps = 0;
        double avg_score = 0;

        for (int i = 0; i < Processing.games.size(); i++) {
            Snake snake = Processing.games.get(i).snake;
            if (snake.died_to == 1) {
                died_to_wall++;
            } else if (snake.died_to == 2) {
                died__to_self++;
            } else {
                died_to_steps++;
            }

            avg_score += snake.fields.size();
        }

        avg_score /= Processing.games.size();

        Random random = new Random();
        long seed = random.nextLong();

        System.out.println("Size of the game board: " + (Processing.size_of_one_game/10));
        System.out.println("Avg length in Generation " + generation + ": " + avg_score);
        System.out.println("Snakes died to wall: " + died_to_wall);
        System.out.println("Snakes died to own body " + died__to_self);
        System.out.println("Snakes died out of steps " + died_to_steps);
        System.out.println("Next seed: " + seed);
        System.out.println();
        System.out.println();

        generation++;

        boolean updateGrid = generation % Processing.generationMultiplier == 0;

        if(updateGrid){
            Processing.multiplyGameSize();
            Processing.getInstance().setup_calculate_game_dimensions();
        }

        //Used for randomizing

        for (Game game : Processing.games) {

            if(updateGrid){
                game.recreateGrid(false);
            }

            //Resets the Snake
            game.snake = new Snake(game,seed);

            //Gets two random parents for the Snakes
            Algo_Generic_AI parent_one = brain_pool.get(random.nextInt(brain_pool.size()));
            Algo_Generic_AI parent_two = brain_pool.get(random.nextInt(brain_pool.size()));


            game.snake.brain = do_crossover_and_mutation(parent_one, parent_two);

        }
    }


    public static Algo_Generic_AI do_crossover_and_mutation(Algo_Generic_AI network_one, Algo_Generic_AI network_two) {

        //We start off by cloning the first network to ensure we don't get any problems with references
        Algo_Generic_AI neural_network_new = clone_neural_network(network_one);

        //Gets the weights of both networks
        double[] weights_of_network_one = get_weights_of_neural_network_as_double(network_one);
        double[] weights_of_network_two = get_weights_of_neural_network_as_double(network_two);

        //Used to randomize stuff
        Random random = new Random();

        //Holds the weights of the new network
        double[] weights_of_new_network = new double[weights_of_network_one.length];

        //Up to this weight, the new nn will have the same weights as nn one, after this one it will have the ones from nn two
        int devider_weight = random.nextInt(weights_of_network_one.length);

        for (int weight_id = 0; weight_id < weights_of_new_network.length; weight_id++) {

            //We take the weights from the first network
            if (weight_id < devider_weight) {
                weights_of_new_network[weight_id] = weights_of_network_one[weight_id];
            }
            //We take the weights form the second network
            else {
                weights_of_new_network[weight_id] = weights_of_network_two[weight_id];
            }

            //We now mutate the weight
            if ((random.nextInt(100) + 1) <= mutation_rate) {
                //We add/substract a random number within the mutation range
                weights_of_new_network[weight_id] += ThreadLocalRandom.current().nextDouble(-mutation_range, +mutation_range);

            }

        }

        //Sets the weights of the new neural network
        neural_network_new.neural_network.setWeights(weights_of_new_network);


        return neural_network_new;
    }

    public static Algo_Generic_AI clone_neural_network(Algo_Generic_AI network_to_clone) {

        //Holds the amount of neurons in each hidden layer
        ArrayList<Integer> network_to_clone_hidden_layer_neuron_count = new ArrayList<Integer>();

        //Fills the ArrayList with data
        for (int hiddenlayer_id = 1; hiddenlayer_id < network_to_clone.neural_network.getLayersCount() - 1; hiddenlayer_id++) {
            //Adds the amount of neurons in each hidden layer to the ArrayList
            network_to_clone_hidden_layer_neuron_count.add(network_to_clone.neural_network.getLayerAt(hiddenlayer_id).getNeuronsCount() - 1);

            /*
                We have to do getNeuronsCount() - 1 as getNeuronsCount() gives us the number of neurons including the BIAS-Neuron. Neuroph automatically
                adds a BIAS-Neuron to a Layer if there isn't any and as we don't specify that the last neuron is a bias neuron we just don't add it to
                the ArrayList as it gets added automatically later anyways
            */

        }
        //New neural network with the same amount of layers and neurons like the network_to_clone, still has random weights
        Algo_Generic_AI new_neural_network = new Algo_Generic_AI(
                network_to_clone.neural_network.getInputsCount(), //Amount of input neurons
                network_to_clone_hidden_layer_neuron_count, //Amount of neurons in each hidden layer
                network_to_clone.neural_network.getOutputsCount()); //Amount of output neurons

        //Sets the weights of the new neural network
        new_neural_network.neural_network.setWeights(get_weights_of_neural_network_as_double(network_to_clone));

        //Returns the neural network
        return new_neural_network;
    }

    public static Algo_Generic_AI generate_new_neural_net_with_other_layers_neurons(Algo_Generic_AI network_original, ArrayList<Integer> hidden_layer_neurons_count) {

        Algo_Generic_AI new_neural_network = new Algo_Generic_AI(
                network_original.neural_network.getInputsCount(), //Amount of input neurons
                hidden_layer_neurons_count, //Amount of neurons in each hidden layer
                network_original.neural_network.getOutputsCount()); //Amount of output neurons

        //We have to generate a Dataset with random inputs and the outputs from our original network
        DataSet trainingSet = new DataSet(network_original.neural_network.getInputsCount(), network_original.neural_network.getOutputsCount());

        Random random = new Random(); //Used to get random inputs

        //Holds a randomized Input
        double[] randomized_input = new double[network_original.neural_network.getInputsCount()];

        //How many rows we want (more rows -> nn has more data to train on
        int rows_of_data = 500000;

        //We create rows for the training set
        for (int trainingSet_row_id = 0; trainingSet_row_id < rows_of_data; trainingSet_row_id++) {

            //Gets a new random input
            randomized_input = generate_random_input(network_original.neural_network.getInputsCount());

            //Hands the inputs to the original network, and processes it
            network_original.neural_network.setInput(randomized_input);
            network_original.neural_network.calculate();
            double[] original_network_result = network_original.neural_network.getOutput();

            //Adds the row to the trainingSet
            trainingSet.addRow(randomized_input, original_network_result);
        }

        //1 second -> 1.000 milliseconds
        //Tests if the ai now gives us similar enough results
        long test_results_every_milliseconds = 1000;

        //By how much we allow the new neural network to be off
        double allowed_error = 0.0001;

        /*
            We tell the AI to learn in a new thread. If we do so our program will continue running. This allows us to have a
            loop that once done, the ai stops learning.
        */

        //Keeps track if we are done learning, this is true once we hit the right precision 3 times in a row
        boolean done_learning = false;

        while(!done_learning) {

            new_neural_network.neural_network.learnInNewThread(trainingSet);

            //The time we started learning
            long staringTime = System.currentTimeMillis();

            //We learn until we learned for the given amount of time
            while (System.currentTimeMillis() < staringTime + test_results_every_milliseconds) ;

            //We tell the network to stop learning
            new_neural_network.neural_network.stopLearning();


            //Now we test 3 random inputs and check if all 3 of them are same (up to the allowed error)
            for(int i = 0; i < 3; i++){

                //Random input
                double[] rdm_input = generate_random_input(network_original.neural_network.getInputsCount());

                //Processes the input for the original network
                network_original.neural_network.setInput(rdm_input);
                network_original.neural_network.calculate();
                double[] network_original_output = network_original.neural_network.getOutput();

                //Processes the input for the new network
                new_neural_network.neural_network.setInput(rdm_input);
                new_neural_network.neural_network.calculate();
                double[] network_new_output = network_original.neural_network.getOutput();

                //Checks if the outputs are up to the allowed error the same
                for(int output_id = 0; output_id < network_original_output.length; output_id++){

                    //Calculates the error
                    double error = Math.abs(network_original_output[output_id] - network_new_output[output_id]);

                    //Our error is still to big -> We have to train again
                    if(error > allowed_error){
                        break;
                    }
                    //If we are on the last testing iteration and our error is small enough -> We are done with the learning
                    if(i == 2 && error < allowed_error){
                        done_learning = true;
                    }
                }
            }
        }

        //Returns the new neural network
        return new_neural_network;
    }

    public static double[] get_weights_of_neural_network_as_double(Algo_Generic_AI network) {

        //Gets all weight of the network to clone as double, we have to create an array of weights out of that to add these to our new network
        Double[] weights_of_network_to_clone_as_double = network.neural_network.getWeights();

        //Stores all weights of the network to clone
        double[] weights_of_network_to_clone = new double[network.neural_network.getWeights().length];

        //Fills the array with data
        for (int weight_id = 0; weight_id < weights_of_network_to_clone.length; weight_id++) {
            weights_of_network_to_clone[weight_id] = weights_of_network_to_clone_as_double[weight_id]; //Converts Double to double
        }

        return weights_of_network_to_clone;

    }

    public static double[] generate_random_input(int input_length){

        //Used for randomizing
        Random random = new Random();

        //Stores the random input
        double[] randomized_input = new double[input_length];

        //Generates one random input
        for (int input_id = 0; input_id < randomized_input.length; input_id++) {

            //Generates a random
            randomized_input[input_id] = random.nextDouble(); //Generates a random number between 0 and 1
        }

        return randomized_input;
    }

}
