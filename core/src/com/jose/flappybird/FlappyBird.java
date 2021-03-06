package com.jose.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
    private Preferences preferences;
    private SpriteBatch batch;
    private Texture background;
    private Texture gameOver;
    //private ShapeRenderer shapeRenderer;

    private Texture[] birds;
    private int flapState = 0;
    private float birdY = 0;
    private float velocity = 0;
    private Circle birdCircle;
    private int score = 0;
    int scoringTube = 0;
    private BitmapFont font;

    private int gameState = 0;
    private float gravity = 2;

    private Texture topTube;
    private Texture bottomTube;
    private Rectangle topTubeRectangle;
    private Rectangle bottomTubeRectangle;
    private float gap = 400;
    private float maxTubeOffset;
    private float tubeVelocity = 4;
    private float maxTubeVelocity = 40;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] topTubeY = new float[numberOfTubes];
    private float[] bottomTubeY = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;

    private Random randomGenerator;


    @Override
    public void create() {
        preferences = Gdx.app.getPreferences("Flappy Bird Preferences");
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameOver = new Texture("gameover.png");
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10f);

        birds = new Texture[2];
        birds[0] = new Texture("chong.png");
        birds[1] = new Texture("bird2.png");


        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        topTubeRectangle = new Rectangle();
        bottomTubeRectangle = new Rectangle();
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 7 / 8;

        startGame();
    }

    private void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

        }
    }

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                if(score % 5 == 0 && tubeVelocity <= maxTubeVelocity){
                    tubeVelocity +=2;
                }

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }
            }

            if (Gdx.input.justTouched()) {

                velocity = -20;

            }

            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                topTubeY[i] = Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i];

                bottomTubeY[i] = Gdx.graphics.getHeight() / 2 - gap / 2 - tubeOffset[i];

                float topTubeHeight = Gdx.graphics.getHeight() - topTubeY[i];
                float bottomTubeHeight = Gdx.graphics.getHeight() - bottomTubeY[i] - gap;

                batch.draw(topTube, tubeX[i], topTubeY[i], topTube.getWidth(), topTubeHeight);
                batch.draw(bottomTube, tubeX[i], 0, bottomTube.getWidth(), bottomTubeHeight);

                topTubeRectangle.set(tubeX[i], topTubeY[i], topTube.getWidth(), topTubeHeight);
                bottomTubeRectangle.set(tubeX[i], 0, bottomTube.getWidth(), bottomTubeHeight);

                if (Intersector.overlaps(birdCircle, topTubeRectangle) || Intersector.overlaps(birdCircle, bottomTubeRectangle)) {

                    gameState = 2;
                    saveScore(score);
                    tubeVelocity = 4;

                }

            }

            if (birdY > 0) {

                velocity = velocity + gravity;
                birdY -= velocity;

            } else {
                gameState = 2;
                saveScore(score);
                tubeVelocity = 4;
            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;


            }

        } else if (gameState == 2) {

            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            saveScore(score);

            if (Gdx.input.justTouched()) {

                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
                tubeVelocity = 4;

            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[0], Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2, birdY);
        font.draw(batch, String.valueOf(preferences.getInteger("highScore",0)), 100, Gdx.graphics.getHeight() - 250);
        font.draw(batch, String.valueOf(score), 100, 250);
        batch.end();
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
    }

    private void saveScore(int score) {
        if (preferences.getInteger("highScore", 0) < score) {
            preferences.putInteger("highScore", score);
            preferences.flush();
        }
    }
}
