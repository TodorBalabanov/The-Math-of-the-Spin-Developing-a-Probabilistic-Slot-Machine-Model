import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

final class Game {
	private static final Random PRNG = new SecureRandom();

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
					int substitutions = 0;
					for (int k = 0; k < view[i].length; k++) {
						if (view[i][k] != Symbol.WILD) {
							substitutions++;
						}
						view[i][k] = Symbol.WILD;
					}
					if (substitutions > 0) {
						expanded++;
					}
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

	private boolean singleFreeSpin(Symbol[][] view, int expanded) {
		statistics.totalNumberOfFreeGames.merge(state, 1L, Long::sum);
		spin(model.cumulatives.get(state), view);
		expanded += wildExpansion(view);
		expanded--;

		boolean hit = false;
		int win = linesWin(view);
		if (win > 0) {
			hit = true;
			statistics.freeHitFrequency.merge(state, 1L, Long::sum);
			statistics.freeMoney.merge(state, (long) win, Long::sum);
			statistics.wonMoney += win;
		}

		statistics.freeWinHistograms.get(state).merge(win, 1L, Long::sum);

		if (state == State.FREE_SPINS_1 && expanded > 0) {
			state = State.FREE_SPINS_2;
			hit |= singleFreeSpin(view, expanded);
		} else if (state == State.FREE_SPINS_2 && expanded > 0) {
			state = State.FREE_SPINS_3;
			hit |= singleFreeSpin(view, expanded);
		} else if (state == State.FREE_SPINS_3 && expanded > 0) {
			model.valid = false;
		} else {
			state = State.BASE_GAME;
		}

		return hit;
	}

	private void singleBaseGame(Symbol[][] view) {
		state = State.BASE_GAME;

		statistics.totalNumberOfBaseGames++;
		spin(model.baseCumulatives, view);
		int expanded = wildExpansion(view);

		int win = linesWin(view);
		if (win > 0) {
			statistics.hitFrequency++;
			statistics.baseHitFrequency++;
			statistics.baseMoney += win;
			statistics.wonMoney += win;
		}

		statistics.baseWinHistogram.merge(win, 1L, Long::sum);

		if (expanded > 3) {
			model.valid = false;
		} else if (expanded > 0) {
			state = State.FREE_SPINS_1;
			boolean hit = singleFreeSpin(view, expanded);
			if (win <= 0 && hit == true) {
				statistics.hitFrequency++;
			}
		}
	}

	void simulate() {
		Symbol[][] view = {
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
				{ null, null, null },
		};

		for (long g = 0L; g < statistics.numberOfBaseGameSpins; g++) {
			statistics.lostMoney += 2 * model.LINES.length;

			singleBaseGame(view);

			if (model.valid == false) {
				statistics = new Statistics();
				statistics.numberOfBaseGameSpins = 1L;
				statistics.totalNumberOfBaseGames = 1L;
				statistics.wonMoney = 100L;
				statistics.lostMoney = 1L;
				break;
			}
		}
	}

	double score() {
		double rtp = (double) statistics.wonMoney / (double) statistics.lostMoney;
		double hitFrequency = (double) statistics.hitFrequency / (double) statistics.numberOfBaseGameSpins;

		return Math.sqrt(100000 * (rtp - RTP_TARGET) * (rtp - RTP_TARGET)
				+ 10000 * (hitFrequency - HIT_FREQUENCY_TARGET) * (hitFrequency - HIT_FREQUENCY_TARGET));
	}

	List<Double> probabilities() {
		List<Double> probabilities = new ArrayList<>();
		for (Map<Symbol, Double> reel : model.baseReels.values()) {
			for (Map.Entry<Symbol, Double> entry : reel.entrySet()) {
				probabilities.add(entry.getValue());
			}
		}
		return probabilities;
	}

	void probabilities(List<Double> probabilities) {
		int index = 0;
		for (Map<Symbol, Double> reel : model.baseReels.values()) {
			for (Map.Entry<Symbol, Double> entry : reel.entrySet()) {
				entry.setValue(probabilities.get(index++));
			}
		}
		model.validate();
		model.normalize();
	}
}
