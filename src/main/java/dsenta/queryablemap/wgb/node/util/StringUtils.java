package dsenta.queryablemap.wgb.node.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class StringUtils {

	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str));
	}
}