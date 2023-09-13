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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {
    Map<String, Game> games = new HashMap<String, Game>();

    private static record NewGameRequest(
        String hostPlayerName, int numberOfPlayers
    ){}

    private static record JoinGameRequest(
    String playerName
    ){}

    private static record JoinGameResponse(
        int numberOfPlayers, List<String> currentPlayers
    ){} 

    private static record NextOrCurrentResponse(
        String clue, int round
    ) {}

    @PostMapping(value="game")
    public ResponseEntity<String> newGame(@RequestBody NewGameRequest request){
        
        Game game = new Game(request.numberOfPlayers);
        game.addPlayer(request.hostPlayerName);
        games.put(game.getCode(), game);

        for (Game g : games.values()) {
            if (g.getTimeStmp() <= System.currentTimeMillis() - 600000) {
                games.remove(g.getCode());
            }
        }

        return new ResponseEntity<String>(game.getCode(), HttpStatus.OK);
    }

    @GetMapping(value="{gameCode}/players")
    public ResponseEntity<List<String>> getPlayers(@PathVariable String gameCode){
        Game game = games.get(gameCode);
        return new ResponseEntity<List<String>>(game.getPlayers(), HttpStatus.OK);
    }

    @PostMapping(value="game/{gameCode}/players")
    public ResponseEntity<JoinGameResponse> addPlayer(@PathVariable String gameCode, @RequestBody JoinGameRequest request){
        Game game = games.get(gameCode);
        game.addPlayer(request.playerName);

        return new ResponseEntity<JoinGameResponse> (new JoinGameResponse(game.getNumberOfPlayers(), game.getPlayers()), HttpStatus.OK);
    }

    @DeleteMapping(value="{gameCode}/players/{player}")
    public void deletePlayer(@PathVariable String gameCode, @PathVariable String player){
        Game game = games.get(gameCode);
        game.removePlayer(player);
    }

    @PostMapping(value="game/{gameCode}/round/{playerName}")
    public ResponseEntity<NextOrCurrentResponse> getNext(@PathVariable String gameCode, @PathVariable String playerName){
        Game game = games.get(gameCode);
        String nxt = game.getNext();

        if(!games.containsKey(gameCode)){
            return ResponseEntity.notFound().build();
        }
        
        return clueOrChameleon(playerName, game, nxt);     
    }

    @GetMapping(value="game/{gameCode}/round/{playerName}")
    public ResponseEntity<NextOrCurrentResponse> getCurrent(@PathVariable String gameCode, @PathVariable String playerName){
        Game game = games.get(gameCode);
        String current = game.getCurrent();
        return clueOrChameleon(playerName, game, current);  
    }

    private ResponseEntity<NextOrCurrentResponse> clueOrChameleon(String playerName, Game game, String nxtOrCur) {
        if(playerName.equals(game.getChameleon())){
            return new ResponseEntity<NextOrCurrentResponse> (new NextOrCurrentResponse("chameleon", game.getRound()), HttpStatus.OK);
        }    

        return new ResponseEntity<NextOrCurrentResponse> (new NextOrCurrentResponse(nxtOrCur, game.getRound()), HttpStatus.OK);
    }    
}
