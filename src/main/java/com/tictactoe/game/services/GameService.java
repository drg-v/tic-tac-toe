package com.tictactoe.game.services;

import java.util.List;
import java.util.Optional;

import com.tictactoe.game.entities.Game;

public interface GameService {
    
    public Integer createGame(String pick);
    
    public Game getGame(Integer id, String side);
    
    public List<Game> getAll();
    
    public Integer joinGame(Integer id);
    
    public void makeMove(Game game, String pick, String value);
    
    public Optional<String> checkWin(Game game);
}
