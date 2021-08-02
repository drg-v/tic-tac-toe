package com.tictactoe.game.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tictactoe.game.entities.Game;
import com.tictactoe.game.models.Move;
import com.tictactoe.game.services.GameService;

@Controller
public class GameController {
    
    @Autowired
    GameService gameService;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Game> games = gameService.getAll();
        List<List<Game>> gamesRows = new LinkedList<>();
        for(int i = 0; i < games.size(); i+=3) {
            int end = i + 3 > games.size() ? games.size() : i + 3;
            gamesRows.add(games.subList(i, end));
        }
        model.addAttribute("gamesRows", gamesRows);
        return "index";
    }
    
    @GetMapping("/games/create")
    public String create() {
        return "create";
    }
    
    @PostMapping("/games/create")
    public String submitCreation(@RequestParam("pick")String pick, HttpSession session) {
        session.setAttribute("side", "home");
        session.setAttribute("id", gameService.createGame(pick));
        return "redirect:/games/play";
    }
    
    @GetMapping("/games/play")
    public String getGame(HttpSession session, Model model) {
        Integer id = (Integer) session.getAttribute("id");
        String side = (String) session.getAttribute("side");
        model.addAttribute("side", side);
        model.addAttribute("id", id);
        model.addAttribute("game", gameService.getGame(id, side));
        return "game";
    }
    
    @PostMapping("/games/join")
    public String joinGame(@RequestParam("id")Integer id, HttpSession session) {
        session.setAttribute("side", "away");
        session.setAttribute("id", gameService.joinGame(id));
        return "redirect:/games/play";
    }
    
    @MessageMapping("/games/play")
    public void send(Move move) {
        Game game = gameService.getGame(move.getId(), move.getSide());
        String pickValue = move.getSide().equals("home") ? "x" : "o";
        gameService.makeMove(game, move.getPick(), pickValue);
        Optional<String> checkWin = gameService.checkWin(game);
        if( checkWin.isEmpty()) {
            Integer receiver = move.getSide().equals("home") ? game.getAway() : game.getHome();
            simpMessagingTemplate.convertAndSendToUser(receiver.toString(), "/queue/moves", move.getPick());
        } else if (checkWin.get().equals("draw")){
            simpMessagingTemplate.convertAndSendToUser(game.getHome().toString(), "/queue/moves", "draw");
            simpMessagingTemplate.convertAndSendToUser(game.getAway().toString(), "/queue/moves", "draw");
        }
        else {
            simpMessagingTemplate.convertAndSendToUser(move.getId().toString(), "/queue/moves", "winner");
            Integer loser = move.getSide().equals("home") ? game.getAway() : game.getHome();
            simpMessagingTemplate.convertAndSendToUser(loser.toString(), "/queue/moves", "loser");
        }
    }
}

