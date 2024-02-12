package com.sharkio.backend.service;

import com.sharkio.backend.model.Mine;
import com.sharkio.backend.model.World;
import com.sharkio.backend.repository.MineRepository;
import com.sharkio.backend.repository.WorldRepository;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Data
@Service
public class MineService {
    /******************************************************************************************************************/
    /*                                                 ATTRIBUTES                                                     */
    /******************************************************************************************************************/
    @Autowired
    private MineRepository repository;
    @Autowired
    private WorldRepository worldRepository;

    /******************************************************************************************************************/
    /*                                                 MAIN FUNCTIONS                                                 */
    /******************************************************************************************************************/
    public Iterable<Mine> getMines() {
        return this.repository.findAll();
    }

    public Mine getById(Integer id) {
        // return mine if exists or throw exception
        return this.repository.findById(id).orElseThrow(() ->
                new RuntimeException("Mine with id " + id + " not found"));
    }

    public Mine addMine(Mine mine) {
        // Assert no null mine are added
        if(mine != null) {
            // Assert mine is not already in the game
            for (Mine value : this.getMines()) {
                if (value.equals(mine)) {
                    throw new RuntimeException("Not allowed to add existing mine");
                }
            }
            // Save mine
            return this.repository.save(mine);
        }
        throw new RuntimeException("Not allowed to add null mine");
    }

    public void delete(Integer id) {
        Mine mine = this.getById(id);
        // Remove mine from world first to avoid error
        World world = this.worldRepository.findAll().iterator().next();
        Set<Mine> mines = world.getMines();
        mines.remove(mine);

        // update in database
        this.worldRepository.save(world);
        this.repository.delete(mine);
    }
}
