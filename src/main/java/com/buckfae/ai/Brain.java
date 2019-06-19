package com.buckfae.ai;

public interface Brain {

    //Gets the input_values from a snakes and calculates the next move accordingly
    int calculate_next_move(double[][] input_values);

    //Is called when all snakes are dead and will add new (better) brains to all snakes
    void generate_brains_for_new_generation();
}
