package ru.karpov;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkerService {
  private long workers;

  public void cancelWorker() {
    workers++;
  };

  public void activateWorker() {
    if (workers == 0)
      throw new RuntimeException();
    workers--;
  }



  public boolean isWorkerAvailable() {
    return workers > 0;
  }
}
