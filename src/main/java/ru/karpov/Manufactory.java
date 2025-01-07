package ru.karpov;

public class Manufactory {
  public static void main(String[] args) {
    Simulation s = new Simulation();
    for (String arg : args) {
      System.out.println("Running simulation for \"" + arg + "\"");
      s.run(arg);
    }
  }
}
