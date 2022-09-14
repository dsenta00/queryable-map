package dsenta.queryablemap.statistics;

import dsenta.queryablemap.QueryableMap;
import dsenta.queryablemap.testutil.RandomGenerator;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TreeStatisticsByTreeName extends TreeMap<String, LinkedList<TreeStatistics>> {
    private static final long serialVersionUID = -9203494215891793300L;
    private Map<String, String> randomNumbers = Map.of();

    public TreeStatisticsByTreeName set(int startAmountOfData) {
        this.randomNumbers = RandomGenerator.streamRandomNumbers(startAmountOfData)
                .collect(Collectors.toMap(Object::toString, Object::toString));
        System.out.println("*************************************************************************");
        System.out.println("  Calculating (" + randomNumbers.size() + " - " + startAmountOfData + ")");
        System.out.println("  Insert time, Get time, Depth, Number Of Nodes, Empty slots");
        return this;
    }

    public TreeStatisticsByTreeName calculateTree(QueryableMap<String, String> queryableMap) {
        if (Objects.isNull(this.getOrDefault(queryableMap.getName(), null))) {
            this.put(queryableMap.getName(), new LinkedList<>());
        }

        var treeStatistics = GetTreeStatistics.getTreeStatistics(queryableMap, this.randomNumbers);

        System.out.print("  " + treeStatistics);

        this.get(queryableMap.getName()).addLast(treeStatistics);
        queryableMap.clear();
        return this;
    }
}