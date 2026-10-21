package com.peter.libgdxlearning;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private Player player;
    private ArrayList<Enemy> enemyList;
    private Weapon weapon;
    private NutProjectile testNut;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        player = new Player(100, 100, 200, 70, 50, 100, 100, 15, 15);
        enemyList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            enemyList.add(new Enemy(600, 400 - 80 * i, 100, 70, 50, 30f));
        }
        testNut = new NutProjectile(
            0f, 0f, new Vector2(1f, 2f), 200f, 6f
        );
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if(!player.isDead()){
            update(delta);
            //weapon.update(delta, player, enemyList);
            removeDeadEnemies();
            checkCollisions();
        }
        draw(); 
    }



    private void update(float delta){
        player.update(delta);

        for(Enemy enemy : enemyList){
            enemy.update(delta, player.getPosition());
        }

        testNut.update(delta);
    }

    private void checkCollisions(){
        for (Enemy enemy : enemyList){
            if(enemy.getHitbox().overlaps(player.getHitbox())){
                player.takeDamage(10f);
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        player.render(shapeRenderer);

        for(Enemy enemy : enemyList){
            enemy.render(shapeRenderer);
        }

        testNut.render(shapeRenderer);

        shapeRenderer.end();
    }


    private void removeDeadEnemies(){
        for(int i = enemyList.size() - 1; i >= 0; i--){
            if(enemyList.get(i).isDead()){
                enemyList.remove(i);
            }
        }
    }



    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
