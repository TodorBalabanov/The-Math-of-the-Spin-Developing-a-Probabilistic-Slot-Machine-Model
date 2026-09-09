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

	Map<Integer, Map<Symbol, Double>> baseCumulatives = new HashMap<>();

	Map<Integer, Map<Symbol, Double>> free1Cumulatives = baseCumulatives;

	Map<Integer, Map<Symbol, Double>> free2Cumulatives = baseCumulatives;

	Map<Integer, Map<Symbol, Double>> free3Cumulatives = baseCumulatives;

	Map<State, Map<Integer, Map<Symbol, Double>>> reels = new HashMap<>();

	Map<State, Map<Integer, Map<Symbol, Double>>> cumulatives = new HashMap<>();

	Map<Symbol, List<Integer>> payTable = new HashMap<>();

	void normalize(Map<Symbol, Double> probability, Map<Symbol, Double> cumulative) {
		double sum = 0D;
		for (Map.Entry<Symbol, Double> entry : probability.entrySet()) {
			sum += entry.getValue();
		}

		cumulative.clear();
		double cumulativeValue = 0D;
		for (Map.Entry<Symbol, Double> entry : probability.entrySet()) {
			entry.setValue(entry.getValue() / sum);

			cumulativeValue += entry.getValue();
			cumulative.put(entry.getKey(), cumulativeValue);
		}
	}

	Model() {
		for (int i = 0; i < 5; i++) {
			HashMap<Symbol, Double> probability;
			baseReels.put(i, probability = new HashMap<>());
			HashMap<Symbol, Double> cumulative;
			baseCumulatives.put(i, cumulative = new HashMap<>());

			Symbol[] symbols = Symbol.values();
			for (int j = 0; j < symbols.length; j++) {
				if ((i == 0 || i == 4) && symbols[j] == Symbol.WILD) {
					probability.put(symbols[j], 0D);
				} else {
					probability.put(symbols[j], 1D);
				}
			}

			normalize(probability, cumulative);
		}

		reels.put(State.BASE_GAME, baseReels);
		reels.put(State.FREE_SPINS_1, free1Reels);
		reels.put(State.FREE_SPINS_2, free2Reels);
		reels.put(State.FREE_SPINS_3, free3Reels);

		cumulatives.put(State.BASE_GAME, baseCumulatives);
		cumulatives.put(State.FREE_SPINS_1, free1Cumulatives);
		cumulatives.put(State.FREE_SPINS_2, free2Cumulatives);
		cumulatives.put(State.FREE_SPINS_3, free3Cumulatives);

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
