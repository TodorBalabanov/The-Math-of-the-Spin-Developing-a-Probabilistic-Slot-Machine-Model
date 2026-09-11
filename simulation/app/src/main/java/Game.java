import java.util.Arrays;
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
				if (state != State.BASE_GAME && view[i][j] == Symbol.WILD) {
					continue;
				}

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

	private int wildExpansion(Symbol[][] view) {
		int expanded = 0;
		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (view[i][j] == Symbol.WILD) {
					for (int k = 0; k < view[i].length; k++) {
						view[i][k] = Symbol.WILD;
					}
					expanded++;
					break;
				}
			}
		}
		return expanded;
	}

	private int lineWin(Symbol[] line) {
		Symbol symbol = line[0];
		if (symbol == Symbol.WILD) {
			model.valid = false;
			return 0;
		}

		int count = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == symbol || line[i] == Symbol.WILD) {
				count++;
			} else {
				break;
			}
		}

		return model.payTable.get(symbol).get(count);
	}

	private int linesWin(Symbol[][] view) {
		int win = 0;
		Symbol[] line1 = { null, null, null, null, null };
		Symbol[] line2 = { null, null, null, null, null };
		for (int l = 0; l < Model.LINES.length; l++) {
			for (int i = 0, j = line2.length - 1; i < line1.length && j >= 0; i++, j--) {
				int index1 = Model.LINES[l][i];
				int index2 = Model.LINES[l][j];
				line1[i] = view[i][index1];
				line2[j] = view[i][index2];
			}

			win += lineWin(line1);
			win += lineWin(line2);
		}

		return win;
	}

	public void simulate() {
		Symbol[][] view = {
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
		};

		state = State.BASE_GAME;
		spin(model.baseCumulatives, view);
		wildExpansion(view);
		System.out.println(linesWin(view));
		System.out.println(
				Arrays.deepToString(view).replace("], [", "],\n [").replace("[", "").replace("]", "").replace(" ", "")
						.replace(",", "\t").replace("Wild", "Wild    "));
	}
}
