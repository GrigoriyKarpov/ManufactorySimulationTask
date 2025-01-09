package ru.karpov;

import java.io.IOException;

public class Manufactory {
  public static void main(String[] args) {
    Simulation s = new Simulation();
    for (String arg : args)
      s.run(arg);
  }
}
