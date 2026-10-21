package com.ENG1C4T3.goose_survivor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ENG1C4T3.goose_survivor.Player;
import java.util.List;

public abstract class Weapon {
    private final float baseDamage;
    private final float baseRange;
    private final float baseAttackInterval;

    private float cooldownRemaining = 0f;

    private float damageCoefficient = 1f;
    private float rangeCoefficient = 1f;
    private float attackSpeedCoefficient = 1f;

    protected Weapon(float baseDamage, 
        float baseRange, float baseAttackInterval){
            if(baseDamage < 0 || baseRange <= 0
                || baseAttackInterval <= 0){
                        throw new IllegalArgumentException(
                            "Base weapon stats should be positive"
                        );
            }
            this.baseDamage = baseDamage;
            this.baseRange = baseRange;
            this.baseAttackInterval = baseAttackInterval;
    }

    protected final float getDamage(){
        return baseDamage * damageCoefficient;
    }

    protected final float getRange(){
        return baseRange * rangeCoefficient;
    }

    protected final float getAttackInterval(){
        return baseAttackInterval / attackSpeedCoefficient;
    }

    public void setDamageCoefficient(float coefficient){
        if(coefficient < 0){
            throw new IllegalArgumentException(
                "Damage coefficient should not be negative"
            );
        }
        damageCoefficient = coefficient;
    }

    public void setRangeCoefficient(float coefficient){
        if(coefficient <= 0f){
            throw new IllegalArgumentException(
                "Range coefficient should be positive"
            );
        }
        rangeCoefficient = coefficient;
    }

    public void setAttackSpeedCoefficient(float coefficient){
        if(coefficient <= 0){
            throw new IllegalArgumentException(
                "Coefficient of attack speed should be positive"
            );
        }
        attackSpeedCoefficient = coefficient;
    }

    protected abstract void attack(Player owner, List<Enemy> enemyList);

    public final void update(float delta, Player owner, List<Enemy> enemyList){

        updateEffects(delta);
        if (owner.isDead()){
            return;
        }

        cooldownRemaining = Math.max(0f, cooldownRemaining - delta);
        if(cooldownRemaining > 0f){
            return;
        }
        attack(owner, enemyList);
        cooldownRemaining = getAttackInterval();
    }

    protected void updateEffects(float delta){

    }
    public void render(ShapeRenderer renderer){

    }

}

    
