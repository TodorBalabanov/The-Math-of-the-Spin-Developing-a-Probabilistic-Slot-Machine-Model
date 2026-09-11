public final class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.simulate();
        // System.out.println(game.model);
        // System.out.println(game.statistics);
        System.out.println("RTP: " + (double) game.statistics.wonMoney / (double) game.statistics.lostMoney);
        System.out.println("Hit Frequency: "
                + (double) game.statistics.hitFrequency / (double) game.statistics.totalNumberOfBaseGames);
    }
}
