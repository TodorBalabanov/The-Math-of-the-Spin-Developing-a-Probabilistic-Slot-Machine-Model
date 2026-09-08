import java.util.HashMap;
import java.util.Map;

final class Statistics {
	long wonMoney = 0L;
	long lostMoney = 0L;
	long baseMoney = 0L;
	long free1Money = 0L;
	long free2Money = 0L;
	long free3Money = 0L;
	long baseHitFrequency = 0L;
	long free1HitFrequency = 0L;
	long free2HitFrequency = 0L;
	long free3HitFrequency = 0L;
	long totalNumberOfBaseGames = 0L;
	long totalNumberOfFree1Games = 0L;
	long totalNumberOfFree2Games = 0L;
	long totalNumberOfFree3Games = 0L;
	Map<Integer, Long> baseWinHistogram = new HashMap<>();
	Map<Integer, Long> free1WinHistogram = new HashMap<>();
	Map<Integer, Long> free2WinHistogram = new HashMap<>();
	Map<Integer, Long> free3WinHistogram = new HashMap<>();
	long numberOfBaseGameSpins = 1_000_000L;
}