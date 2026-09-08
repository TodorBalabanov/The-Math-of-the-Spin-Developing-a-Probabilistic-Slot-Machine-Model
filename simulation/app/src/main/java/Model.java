import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Model {
	private static final int[][] PRIZES = {
			{ 0, 0, 0, 0, 0, 0, 0, 0, },
			{ 0, 0, 0, 0, 0, 0, 0, 0, },
			{ 0, 0, 0, 0, 0, 0, 0, 0, },
			{ 0, 100, 50, 20, 16, 14, 10, 10, },
			{ 0, 400, 120, 50, 40, 30, 20, 20, },
			{ 0, 500, 240, 120, 100, 80, 50, 50, },
	};

	private static final int[][] LINES = {
			{ 1, 1, 1, 1, 1, },
			{ 0, 0, 0, 0, 0, },
			{ 2, 2, 2, 2, 2, },
			{ 0, 1, 2, 1, 0, },
			{ 2, 1, 0, 1, 2, },
			{ 0, 0, 1, 0, 0, },
			{ 2, 2, 1, 2, 2, },
			{ 1, 2, 2, 2, 1, },
			{ 1, 0, 0, 0, 1, },
			{ 1, 0, 1, 0, 1, },
	};

	boolean valid = true;

	Map<Integer, Map<Symbol, Double>> baseReels = new HashMap<>();

	Map<Integer, Map<Symbol, Double>> free1Reels = baseReels;

	Map<Integer, Map<Symbol, Double>> free2Reels = baseReels;

	Map<Integer, Map<Symbol, Double>> free3Reels = baseReels;

	Map<Symbol, List<Integer>> payTable = new HashMap<>();

	void normalize(Map<Symbol, Double> reel) {
		double sum = 0D;
		for (Map.Entry<Symbol, Double> entry : reel.entrySet()) {
			sum += entry.getValue();
		}
		for (Map.Entry<Symbol, Double> entry : reel.entrySet()) {
			entry.setValue(entry.getValue() / sum);
		}
	}

	Model() {
		for (int i = 0; i < 5; i++) {
			HashMap<Symbol, Double> reel;
			baseReels.put(i, reel = new HashMap<>());
			Symbol[] symbols = Symbol.values();
			for (int j = 0; j < symbols.length; j++) {
				if ((i == 0 || i == 4) && symbols[j] == Symbol.WILD) {
					reel.put(symbols[j], 0D);
				} else {
					reel.put(symbols[j], 1D);
				}
			}
			normalize(reel);
		}

		Symbol[] symbols = Symbol.values();
		for (int i = 0; i < symbols.length; i++) {
			List<Integer> payouts = new ArrayList<>();
			for (int count = 0; count < PRIZES.length; count++) {
				payouts.add(PRIZES[count][i]);
			}
			payTable.put(symbols[i], payouts);
		}
	}
}
