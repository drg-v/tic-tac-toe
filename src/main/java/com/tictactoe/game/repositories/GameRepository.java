package com.tictactoe.game.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tictactoe.game.entities.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {
    
    public Game getOneByHome(Integer home);
    
    public Game getOneByAway(Integer home);
    
    public List<Game> findByStatus(String status);
}
