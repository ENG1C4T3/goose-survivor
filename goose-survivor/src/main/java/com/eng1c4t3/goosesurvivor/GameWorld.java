package com.eng1c4t3.goosesurvivor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameWorld {
    private final List<Entity> entities = new ArrayList<>();
    
    /**
    * Adds an entity to this world
    * @param entity  the entity to add
    * @throws IlligealArgumentException if Entity is null
    or already been add 
    */
    public void addEntity(Entity entity){
        if(entity == null){
            throw new IllegalArgumentException(
                "Entity must not be null"
            );
        }
        if(entities.contains(entity)){
            throw new IllegalArgumentException(
                "The same entity should not appear twice."
            );
        }
        
        entities.add(entity);
    }
    
    /**
     * remove a single entity from world
     * @param entity the entity to remove
     */
    public void removeEntity(Entity entity){
        entities.remove(entity);
    }

    /**
     * Returns an unmodifiable view of enities 
     * 
     * in the returned view, the entites and their
      components remain mutable
     *
     * @return an unmodifiable view of entities in the world
     */
    public List<Entity> getEntities(){
        return Collections.unmodifiableList(entities);
    }

}
