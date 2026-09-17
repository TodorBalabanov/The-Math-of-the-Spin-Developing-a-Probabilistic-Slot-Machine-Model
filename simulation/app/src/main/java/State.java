enum State {
	BASE_GAME(0),
	FREE_SPINS_1(1),
	FREE_SPINS_2(2),
	FREE_SPINS_3(3);

	private final int number;

	private State(int number) {
		this.number = number;
	}

	int number() {
		return number;
	}
}
