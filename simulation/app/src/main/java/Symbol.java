import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

enum Symbol {
	WILD(0, "Wild"),
	SYMBOL01(1, "Symbol01"),
	SYMBOL02(2, "Symbol02"),
	SYMBOL03(3, "Symbol03"),
	SYMBOL04(4, "Symbol04"),
	SYMBOL05(5, "Symbol05"),
	SYMBOL06(6, "Symbol06"),
	SYMBOL07(7, "Symbol07");

	private static final Random PRNG = ThreadLocalRandom.current();

	private final int index;
	private final String caption;

	static Symbol random() {
		return values()[PRNG.nextInt(values().length)];
	}

	private Symbol(int index, String caption) {
		this.index = index;
		this.caption = caption;
	}

	int index() {
		return index;
	}

	String caption() {
		return caption;
	}

	@Override
	public String toString() {
		return caption;
	}
}
