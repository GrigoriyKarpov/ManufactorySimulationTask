package ru.karpov;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {
  private final long workersCount = 2;
  private final Simulation simulation = new Simulation();
  private final WorkerService ws = new WorkerService(workersCount);
  private final ProductionCenter pc = new ProductionCenter("test", "test", workersCount, BigDecimal.ONE, ws);

  {
    pc.setBuffer(10);
    pc.setInitPC(pc);
  }

  private final String inputDataPath =
      "C:/Users/grigo/Desktop/Тестовое задание Java-разработчик (ИМ)/тестовый сценарий №1 3 сотрудников.xlsx";
  private final String resultDataFileName = "тестовый сценарий №1 3 сотрудников result.csv";

  @Test
  public void fileNotFoundExceptionTest() {
    IOException e = assertThrows(IOException.class,
        () -> {
          TaskDataReader tdr = new TaskDataReader("not exist path");
        }, "File not exist");
    assertTrue(e.getMessage().contains("not exist"));
  }

  @Test
  public void saveResultTest() {
    if (Files.exists(Paths.get(resultDataFileName)))
      assertTrue((new File(resultDataFileName)).delete(), "Could not delete the file");
    simulation.run(inputDataPath);
    assertTrue(Files.exists(Paths.get(resultDataFileName)), "File not exist");
  }

  @Test
  public void updatePCStateTest() {
    pc.updateState(BigDecimal.ONE);
    assertFalse(ws.isWorkerAvailable(), "Must be no workers");
  }
}
