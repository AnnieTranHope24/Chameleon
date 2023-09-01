package com.mcfall.chameleon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {
    Map<String, Game> games = new HashMap<String, Game>();

    @PostMapping(value="newGame/{player}")
    public ResponseEntity<String> newGame(@PathVariable String player){
        Game game = new Game();
        game.addPlayer(player);
        games.put(game.getCode(), game);

        return new ResponseEntity<String>(game.getCode(), HttpStatus.OK);
    }

    @GetMapping(value="{gameCode}/players")
    public ResponseEntity<List<String>> getPlayers(@PathVariable String gameCode){
        Game game = games.get(gameCode);
        return new ResponseEntity<List<String>>(game.getPlayers(), HttpStatus.OK);
    }

    @PostMapping(value="{gameCode}/addPlayer/{player}")
    public void addPlayer(@PathVariable String gameCode, @PathVariable String player){
        Game game = games.get(gameCode);
        game.addPlayer(player);
    }

    @DeleteMapping(value="{gameCode}/players/{player}")
    public void deletePlayer(@PathVariable String gameCode, @PathVariable String player){
        Game game = games.get(gameCode);
        game.removePlayer(player);
    }

    @PostMapping(value="{gameCode}/{player}/next")
    public String getNext(@PathVariable String gameCode, @PathVariable String player){
        Game game = games.get(gameCode);
        if(player == game.getChameleon()){
            return "chameleon";
        }    

        return game.getNext();        
    }

    @GetMapping(value="{gameCode}/{player}/current")
    public String getCurrent(@PathVariable String gameCode, @PathVariable String player){
        Game game = games.get(gameCode);
        if(player == game.getChameleon()){
            return "chameleon";
        }        

        return game.getCurrent();  
    }
}
