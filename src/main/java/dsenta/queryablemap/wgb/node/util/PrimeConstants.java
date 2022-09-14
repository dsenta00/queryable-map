package dsenta.queryablemap.wgb.node.util;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimeConstants {

    public static final Map<Integer, Integer> NEXT_PRIME = Map.ofEntries(
            Map.entry(2, 3),
            Map.entry(3, 5),
            Map.entry(5, 7),
            Map.entry(7, 11),
            Map.entry(11, 13),
            Map.entry(13, 17),
            Map.entry(17, 19),
            Map.entry(19, 23),
            Map.entry(23, 29),
            Map.entry(29, 31),
            Map.entry(31, 37),
            Map.entry(37, 41),
            Map.entry(43, 47),
            Map.entry(47, 53),
            Map.entry(53, 59),
            Map.entry(59, 61),
            Map.entry(61, 67),
            Map.entry(67, 71),
            Map.entry(71, 73),
            Map.entry(73, 79),
            Map.entry(79, 83),
            Map.entry(83, 89),
            Map.entry(89, 97),
            Map.entry(97, 101),
            Map.entry(101, 103),
            Map.entry(103, 107),
            Map.entry(107, 109),
            Map.entry(109, 113),
            Map.entry(113, 127),
            Map.entry(127, 131),
            Map.entry(131, 137),
            Map.entry(137, 139),
            Map.entry(139, 149),
            Map.entry(149, 151),
            Map.entry(151, 157),
            Map.entry(157, 163),
            Map.entry(163, 167),
            Map.entry(167, 173),
            Map.entry(173, 179),
            Map.entry(179, 181),
            Map.entry(181, 191),
            Map.entry(191, 193),
            Map.entry(193, 197),
            Map.entry(197, 199)
    );

    public static final int FIRST_PRIME = 2;
}