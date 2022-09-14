package dsenta.queryablemap.statistics;

import java.util.Map;
import java.util.Set;

import dsenta.queryablemap.QueryableMap;

public final class GetTreeStatistics {

    private static final int NOOF_TO_TRY = 5;

    public static <T extends Comparable<T>>
    TreeStatistics getTreeStatistics(QueryableMap<T, T> queryableMap, Map<T, T> randomNumbers) {
        var treeStatistics = new TreeStatistics();

        double totalInsert = 0;
        double totalGet = 0;

        for (int i = 0; i < NOOF_TO_TRY; i++) {
            totalInsert += StopWatchExecutor.measureTime(() -> queryableMap.putAll(randomNumbers));
            Set<T> keys = randomNumbers.keySet();
            totalGet += StopWatchExecutor.measureTime(() -> keys.forEach(queryableMap::containsKey));
            treeStatistics.setDepth(queryableMap.depth());
            treeStatistics.setNumberOfNodes(queryableMap.getNumberOfNodes());
            treeStatistics.setNumberOfEmptyNodes(queryableMap.getNumberOfEmptyNodes());
            queryableMap.clear();
        }

        treeStatistics.setInsertTime(totalInsert / NOOF_TO_TRY);
        treeStatistics.setGetTime(totalGet / NOOF_TO_TRY);
        return treeStatistics;
    }
}
