package com.tictactoe.game.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Game {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    private Integer home;
    
    private Integer away;
    
    private String status;
    
    private String topLeft;
    
    private String topCenter;
    
    private String topRight;
    
    private String centerLeft;
    
    private String centerCenter;
    
    private String centerRight;
    
    private String lowLeft;
    
    private String lowCenter;
    
    private String lowRight;
    
}   
