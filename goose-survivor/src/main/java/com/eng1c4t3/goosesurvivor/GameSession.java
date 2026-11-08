package com.eng1c4t3.goosesurvivor;


public class GameSession {
    private static final float TIME_LIMIT = 300f;
    private SessionState gameState = SessionState.RUNNING;
    private float elapsedTime = 0f;

    private final GameWorld world;


    public GameSession(GameWorld world){
        if (world == null){
            throw new IllegalArgumentException(
                "Game world should not be null"
            );
        }
        this.world = world;
    }

    public GameWorld getWorld(){
        return world;
    }


    public SessionState getState(){
        return gameState;
    }

    public float getRemainingTime(){
        return Math.max(0f, TIME_LIMIT - elapsedTime);
    }

    public void pause(){
        if(gameState == SessionState.RUNNING){
            gameState = SessionState.PAUSED;
        }
    }

    public void resume(){
        if(gameState == SessionState.PAUSED){
            gameState = SessionState.RUNNING;
        }
    }

    /**
     * Advance timer while game is running
     * @param deltaTime elapes time in seconds
     * @throws IllegalArgumentException if deltaTime is negative
     */
    public void update(float deltaTime){
        if (deltaTime < 0f){
            throw new IllegalArgumentException(
                "Delta time must not be negative"
            );
        }
        if(gameState != SessionState.RUNNING){
            return;
        }

        elapsedTime += deltaTime;

        if(elapsedTime >= TIME_LIMIT){
            elapsedTime = TIME_LIMIT;
            lose();
        }

        //TODO: Update gameplay system

    }

    public void win(){
        if(gameState == SessionState.RUNNING){
            gameState = SessionState.WON;
        }
    }

    public void lose(){
        if(gameState == SessionState.RUNNING){
            gameState = SessionState.LOST;
        }
    }

}

