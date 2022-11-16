package fr.uge.patchwork.main;

import fr.uge.patchwork.*;

public class Main {
  public static void main(String[] args) {

    //var game = new Game();
    //game.start();

    String piece = "***,**,****";
    Piece p = new Piece(1,1,1,piece, 1);
    System.out.println(p);
//    System.out.println(p);
//    p = p.rotate90();
//    System.out.println(p);
//    p = p.rotate90();
//    System.out.println(p);
//    p = p.rotate90();
//    System.out.println(p);
//    p = p.rotate90();
//    System.out.println(p);
    p = p.flip();
    System.out.println(p);
    p = p.rotate90();
    System.out.println(p);


  }
}