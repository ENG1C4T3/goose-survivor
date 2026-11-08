package com.eng1c4t3.goosesurvivor;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Provides access to entities stared in Ashley engine
 */
public class GameWorld{
    private final Engine engine;

    /**
     * Creates a world backed by the given engine
     * 
     * @param engine that stares world's entities
     * @throws IllegalArgumentException if engine is null
     * 
     */
    public GameWorld(Engine engine){
        if (engine == null){
            throw new IllegalArgumentException(
                "Engine must not be null"
            );
        }
        this.engine = engine;
    }

    /**
     * 
     * Add an entity to this world
     * @param entity  the entity to add
     * @throws IllegalArgumentException if Entity is null
     or already been add 
     */
    public void addEntity(Entity entity){
        if(entity == null){
            throw new IllegalArgumentException(
                "Entity must not be null"
            );
        }
        if(engine.getEntities().contains(entity, true)){
            throw new IllegalArgumentException(
                "The same entity should not appear twice."
            );
        }
        
        engine.addEntity(entity);
    }
    
    /**
     * remove a single entity from world
     * @param entity the entity to remove
     */
    public void removeEntity(Entity entity){
        engine.removeEntity(entity);
    }

    /**
     * Return a read-only view of this world's entites
     * 
     * in the returned view, the entites and their
      components remain mutable
     *
     * @return an unmodifiable view of entities in the world
     */
    public ImmutableArray<Entity> getEntities(){
        return engine.getEntities();
    }

}
