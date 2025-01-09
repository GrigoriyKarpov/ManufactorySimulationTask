package ru.karpov;

import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The Production Center is responsible for processing parts
 */
@Getter
@Setter
public class ProductionCenter {
  private final String id;
  private final String name;
  private long buffer = 0; // Number of Parts awaiting processing
  private final long maxWorkersCount; // Maximum number of workers in the Production Center
  private final BigDecimal performance; // Part processing time
  private List<ProductionCenter> dependentPC = new ArrayList<>(); // Links to further Production Centers
  private List<BigDecimal> currentWork = new ArrayList<>();
  private int lastPC = -1;
  private List<BigDecimal> timeBuffer = new ArrayList<>(); // Time buffer for accounting for the remaining time from the previous PC
  public static final BigDecimal NO_WORK = new BigDecimal(-1);
  private ProductionCenter initPC = null;
  WorkerService workerService;

  public ProductionCenter(String id, String name, long maxWorkersCount, BigDecimal performance, WorkerService workerService) {
    this.id = id;
    this.name = name;
    this.maxWorkersCount = maxWorkersCount;
    this.performance = performance;
    this.workerService = workerService;

    // Initializing the current work array
    for (int i = 0; i < maxWorkersCount; i++)
      this.currentWork.add(NO_WORK);
  }

  public void updateState(BigDecimal timeUnit) {
    // Update the status of parts in processing
    for (int i = 0; i < maxWorkersCount; i++) {
      if (currentWork.get(i).compareTo(NO_WORK) == 0 && workerService.isWorkerAvailable() && buffer > 0) {
        startNewWork(i);
      } else if (currentWork.get(i).compareTo(new BigDecimal(0)) >= 0) {
        BigDecimal newTime = currentWork.get(i).add(timeUnit);

        if (newTime.compareTo(performance) >= 0) {
          // If processing of part is done
          newTime = newTime.subtract(performance);

          if (buffer > 0) {
            // In processing a new part taking into account the remaining time
            currentWork.set(i, newTime);
            buffer--;
          } else {
            // No work is being done, one worker is free
            currentWork.set(i, NO_WORK);
            workerService.cancelWorker();
          }

          // Transfer of the finished part to the buffer of the next PC, if there is one
          ProductionCenter pc = getNextPC();
          if (pc != null) {
            pc.setBuffer(pc.getBuffer() + 1);
            if (newTime.compareTo(BigDecimal.ZERO) > 0)
              pc.getTimeBuffer().add(newTime);
          }
        } else {
          // If the part is still being processed
          currentWork.set(i, newTime);
        }
      }
    }

    // Clearing the time remaining buffer
    timeBuffer.clear();

    // Update all child PCs
    for (ProductionCenter pc : dependentPC) {
      pc.updateState(timeUnit);
    }

    // Also start new work if workers become available
    if (dependentPC.isEmpty())
      initPC.updateWorks();
  }

  private void startNewWork(int i) {
    BigDecimal bufTime = !timeBuffer.isEmpty() ? timeBuffer.get(0) : BigDecimal.ZERO;
    if (bufTime.compareTo(performance) > 0) {
      // TODO ...
      throw new UpdateStateException("The time in the buffer must not exceed the performance value");
    }
    currentWork.set(i, BigDecimal.ZERO.add(bufTime));
    buffer--;
    workerService.activateWorker();
    if (!timeBuffer.isEmpty())
      timeBuffer.remove(0);
  }

  public class UpdateStateException extends RuntimeException {
    public UpdateStateException(String message) {
      super(message);
    }
  }

  private void updateWorks() {
    for (int i = 0; i < maxWorkersCount; i++)
      if (currentWork.get(i).equals(NO_WORK) && workerService.isWorkerAvailable() && buffer > 0)
        startNewWork(i);

    for (ProductionCenter pc : dependentPC)
      pc.updateWorks();
  }

  // Consistently returns a link to the next child PC
  private ProductionCenter getNextPC() {
    if (dependentPC.isEmpty())
      return null;
    lastPC++;
    if (lastPC >= dependentPC.size())
      lastPC = 0;
    return dependentPC.get(lastPC);
  }

  public boolean isDone() {
    if (buffer != 0)
      return false;

    for (ProductionCenter pc : dependentPC)
      if (!pc.isDone())
        return false;

    return true;
  }

  public int getWorkersCount () {
    int count = 0;
    for (BigDecimal work : currentWork)
      if (work.compareTo(NO_WORK) != 0)
        count++;
    return count;
  }

  public String getWorkState(DecimalFormat df) {
    StringBuilder sb = new StringBuilder("[");
    for (BigDecimal d : currentWork) {
      sb.append(df.format(d))
          .append(", ");
    }
    if (sb.length() > 4)
      sb.delete(sb.length() - 2, sb.length());
    sb.append("]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ProductionCenter && this.id != null && this.id.equals(((ProductionCenter) obj).id);
  }
}
