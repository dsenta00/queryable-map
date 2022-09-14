package dsenta.queryablemap.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreeStatistics {
    private double insertTime;
    private double getTime;
    private int depth;
    private int numberOfNodes;
    private int numberOfEmptyNodes;

    public double totalTime() {
        return insertTime + getTime;
    }

    @Override
    public String toString() {
        return insertTime +
                "," + getTime +
                "," + depth +
                "," + numberOfNodes +
                "," + numberOfEmptyNodes + "\n";
    }
}
