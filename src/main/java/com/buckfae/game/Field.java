package com.buckfae.game;

import java.awt.*;

import static java.awt.Color.*;

public class Field {

    int field_x;
    int field_y;

    public boolean is_snake = false;
    public boolean is_food = false;
    public boolean isSnakeHead = false;


    Game game;

    //Color of the field
    Color color_fill;

    //Size of one field
    public static int field_size = 10;

    public Field(int field_x, int field_y, Game game) {

        this.field_x = field_x;
        this.field_y = field_y;

        this.color_fill = white;

        this.game = game;

    }

    public void draw_field(){

        //Sets the stroke of the field
        Processing.processing.stroke(black.getRGB());

        //Sets the color of the field depending on the type
        if (isSnakeHead){
            Processing.processing.fill(blue.getRGB());
        } else if(is_snake){
            Processing.processing.fill(red.getRGB());
        } else if (is_food) {
            Processing.processing.fill(green.getRGB());
        } else {
            Processing.processing.fill(white.getRGB());
        }

        //Translates the position of the field according to the position of the game
        Processing.processing.rect(field_x + game.game_x, field_y + game.game_y, field_size, field_size);

    }
}
