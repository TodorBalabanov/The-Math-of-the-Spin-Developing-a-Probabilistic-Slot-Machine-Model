import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

final class SlotGame {
	private static final Random PRNG = ThreadLocalRandom.current();

	static final double RTP_TARGET = 0.961;

	public Model model = new Model();

	public Statistics statistics = new Statistics();

	public State state = State.BASE_GAME;
}
