import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EliteSelector;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;

public final class Main {
    static final Random PRNG = ThreadLocalRandom.current();

    static final int POPULATION_SIZE = 53;

    static final int NUMBER_OF_GENERATIONS = 300;

    static final double STOP_THRESHOLD = 0.01D;

    private static double evaluation(Genotype<DoubleGene> genotype) {
        Game game = new Game();

        List<Double> probabilities = game.probabilities();
        DoubleChromosome chromosome = genotype.chromosome().as(DoubleChromosome.class);
        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, chromosome.get(i).doubleValue());
        }

        game.probabilities(probabilities);
        game.simulate();

        if (game.model.valid == false) {
            return Integer.MAX_VALUE;
        }

        return game.score();
    }

    public static void main(String[] args) {
        Game game = new Game();

        List<Double> probabilities = game.probabilities();
        Factory<Genotype<DoubleGene>> factory = Genotype.of(
                DoubleChromosome.of(0D, 1D, probabilities.size()));

        Engine<DoubleGene, Double> engine = Engine.builder(Main::evaluation, factory)
                .populationSize(POPULATION_SIZE)
                .optimize(Optimize.MINIMUM)
                .survivorsFraction(0.05)
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        new UniformCrossover<>(0.5),
                        new Mutator<>(0.05))
                .build();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        Genotype<DoubleGene> result = engine.stream().limit(Limits.byFitnessThreshold(STOP_THRESHOLD))
                .limit(NUMBER_OF_GENERATIONS).peek(intermediate -> {
                    System.out.println(LocalTime.now() + "\t" +
                            intermediate.generation() + "\t" +
                            intermediate.bestFitness());
                }).peek(statistics).collect(EvolutionResult.toBestGenotype());

        DoubleChromosome chromosome = result.chromosome().as(DoubleChromosome.class);
        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, chromosome.get(i).doubleValue());
        }

        game.probabilities(probabilities);
        game.simulate();

        System.out.println(statistics);
        System.out.println(game.model);
        System.out.println(game.statistics);
        System.out.println("RTP: " + (double) game.statistics.wonMoney / (double) game.statistics.lostMoney);
        System.out.println("Hit Frequency: "
                + (double) game.statistics.hitFrequency / (double) game.statistics.totalNumberOfBaseGames);
    }
}
