package ru.karpov;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorkerService {
  private long workers;

  public void cancelWorker() {
    workers++;
  };

  public void activateWorker() throws Exception {
    if (workers == 0)
      throw new Exception();
    workers--;
  }

  public boolean isWorkerAvailable() {
    return workers > 0;
  }
}
