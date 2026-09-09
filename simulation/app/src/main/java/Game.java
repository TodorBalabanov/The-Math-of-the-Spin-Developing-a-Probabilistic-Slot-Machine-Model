import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

final class Game {
	private static final Random PRNG = ThreadLocalRandom.current();

	static final double RTP_TARGET = 0.961;

	static final double HIT_FREQUENCY_TARGET = 0.226;

	Model model = new Model();

	Statistics statistics = new Statistics();

	State state = State.BASE_GAME;

	private void spin(Map<Integer, Map<Symbol, Double>> cumulatives, Symbol[][] view) {
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				double randomValue = PRNG.nextDouble();
				Map<Symbol, Double> cumulative = cumulatives.get(i);
				for (Map.Entry<Symbol, Double> entry : cumulative.entrySet()) {
					if (randomValue <= entry.getValue()) {
						view[i][j] = entry.getKey();
						break;
					}
				}
			}
		}
	}

	public void simulate() {
		Symbol[][] view = {
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
		};

		spin(model.baseCumulatives, view);
		System.out.println(Arrays.deepToString(view));
	}
}
