import java.util.HashMap;
import java.util.Map;

final class Statistics {
	long wonMoney = 0L;
	long lostMoney = 0L;
	long baseMoney = 0L;
	long baseHitFrequency = 0L;
	long totalNumberOfBaseGames = 0L;
	Map<Integer, Long> baseWinHistogram = new HashMap<>();
	Map<State, Long> freeMoney = new HashMap<>();
	Map<State, Long> freeHitFrequency = new HashMap<>();
	Map<State, Long> totalNumberOfFreeGames = new HashMap<>();
	Map<State, Map<Integer, Long>> freeWinHistograms = new HashMap<>();
	long numberOfBaseGameSpins = 1_000_000L;

	Statistics() {
		freeMoney.put(State.FREE_SPINS_1, 0L);
		freeMoney.put(State.FREE_SPINS_2, 0L);
		freeMoney.put(State.FREE_SPINS_3, 0L);

		freeHitFrequency.put(State.FREE_SPINS_1, 0L);
		freeHitFrequency.put(State.FREE_SPINS_2, 0L);
		freeHitFrequency.put(State.FREE_SPINS_3, 0L);

		totalNumberOfFreeGames.put(State.FREE_SPINS_1, 0L);
		totalNumberOfFreeGames.put(State.FREE_SPINS_2, 0L);
		totalNumberOfFreeGames.put(State.FREE_SPINS_3, 0L);

		freeWinHistograms.put(State.FREE_SPINS_1, new HashMap<>());
		freeWinHistograms.put(State.FREE_SPINS_2, new HashMap<>());
		freeWinHistograms.put(State.FREE_SPINS_3, new HashMap<>());
	}

	@Override
	public String toString() {
		return "Statistics {\n" +
				"  wonMoney = " + wonMoney + "\n" +
				"  lostMoney = " + lostMoney + "\n" +
				"  baseMoney = " + baseMoney + "\n" +
				"  baseHitFrequency = " + baseHitFrequency + "\n" +
				"  totalNumberOfBaseGames = " + totalNumberOfBaseGames + "\n" +
				"  baseWinHistogram = " + baseWinHistogram + "\n" +
				"  freeMoney = " + freeMoney + "\n" +
				"  freeHitFrequency = " + freeHitFrequency + "\n" +
				"  totalNumberOfFreeGames = " + totalNumberOfFreeGames + "\n" +
				"  freeWinHistograms = " + freeWinHistograms + "\n" +
				"  numberOfBaseGameSpins = " + numberOfBaseGameSpins + "\n" +
				"}";
	}

}