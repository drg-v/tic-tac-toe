package com.tictactoe.game.services;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tictactoe.game.entities.Game;
import com.tictactoe.game.repositories.GameRepository;

@Service
public class GameServiceImpl implements GameService {
    
    @Autowired
    GameRepository gameRepository;
    
    private Integer generateIdentificator() {
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secureRandom.nextInt();
    }
    
    private void setField(Game game, String pick, String value) {
            try {
                Field field = game.getClass().getDeclaredField(pick);
                field.setAccessible(true);
                field.set(game, value);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
    }
    
    @Override
    public Integer createGame(String pick) {
        int homeId = generateIdentificator();
        Game game = new Game();
        game.setHome(homeId);
        setField(game, pick, "x");
        game.setStatus("created");
        gameRepository.save(game);
        return homeId;
    }

    @Override
    public Game getGame(Integer id, String side) {
        return side.equals("home") ? gameRepository.getOneByHome(id) : gameRepository.getOneByAway(id);
    }

    @Override
    public List<Game> getAll() {
        return gameRepository.findByStatus("created");
        
    }

    @Override
    public Integer joinGame(Integer id) {
        Game game = gameRepository.getById(id);
        Integer awayId = generateIdentificator();
        game.setAway(awayId);
        game.setStatus("started");
        gameRepository.save(game);
        return awayId;
    }

    @Override
    public void makeMove(Game game, String pick, String value) {
        setField(game, pick, value);
        gameRepository.save(game);
    }
    
    private String[][] toArray(Game game) {
        String [][] gameField = new String[3][3];
        gameField[0][0] = game.getTopLeft() == null ? "" : game.getTopLeft();
        gameField[0][1] = game.getTopCenter() == null ? "" :  game.getTopCenter();
        gameField[0][2] = game.getTopRight() == null ? "" :  game.getTopRight(); 
        gameField[1][0] = game.getCenterLeft() == null ? "" :  game.getCenterLeft(); 
        gameField[1][1] = game.getCenterCenter() == null ? "" :  game.getCenterCenter(); 
        gameField[1][2] = game.getCenterRight() == null ? "" :  game.getCenterRight();
        gameField[2][0] = game.getLowLeft() == null ? "" :  game.getLowLeft();
        gameField[2][1] = game.getLowCenter()  == null ? "" :  game.getLowCenter();
        gameField[2][2] = game.getLowRight()  == null ? "" :  game.getLowRight();
        return gameField;
    }
    
    public boolean isDraw(String[][] gameField) {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(gameField[i][j].equals("")) return false;
            }
        }
        return true;
    }
    
    @Override
    public Optional<String> checkWin(Game game) {
        String [][] gameField = toArray(game);
        for(int i = 0; i < 3; i++) {
            int row = 0, col = 0, lDiag = 0, rDiag = 0;
            for(int j = 1; j < 3; j++) {
                if(gameField[i][j].equals(gameField[i][j-1]) && !gameField[i][j].equals("")) row++;
                if(gameField[j][i].equals(gameField[j-1][i]) && !gameField[j][i].equals("")) col++;
                if(i == 0 && gameField[j][j].equals(gameField[j-1][j-1]) && !gameField[j][j].equals("")) lDiag++;
                if(i == 0 && gameField[j][2-j].equals(gameField[j-1][2 - j + 1]) && !gameField[j][2-j].equals("")) rDiag++;
            }
            if(row == 2) return Optional.of(gameField[i][0]);
            if(col == 2) return Optional.of(gameField[i][0]);
            if(lDiag == 2) return Optional.of(gameField[0][0]);
            if(rDiag == 2) return Optional.of(gameField[2][0]);
        }
        return isDraw(gameField) ? Optional.of("draw") : Optional.empty();
    }
    
}
