package ru.karpov;

import org.junit.Test;

public class SimulationTest {
  @Test
  public void test() {
    Simulation s = new Simulation();
    s.run("C:/Users/grigo/Desktop/Тестовое задание Java-разработчик (ИМ)/тестовый сценарий №1 3 сотрудников.xlsx");
    s.run("C:/Users/grigo/Desktop/Тестовое задание Java-разработчик (ИМ)/тестовый сценарий №1 6 сотрудников.xlsx");
    assert(true);
  }
}
