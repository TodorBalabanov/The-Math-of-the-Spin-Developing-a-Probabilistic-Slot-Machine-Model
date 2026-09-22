import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
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
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;

public final class Main {
    private static final int POPULATION_SIZE = 113;

    private static final int NUMBER_OF_GENERATIONS = 10;

    private static final double STOP_THRESHOLD = 0.01D;

    private static final String INTERMEDIATE_FILE = "intermediate.bin";

    private static double bestFitness = Double.MAX_VALUE;

    private static double evaluation(Genotype<DoubleGene> genotype) {
        Game game = new Game();

        List<Double> probabilities = game.probabilities();
        DoubleChromosome chromosome = genotype.chromosome().as(DoubleChromosome.class);
        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, chromosome.get(i).doubleValue());
        }

        if (bestFitness > 10) {
            game.statistics.numberOfBaseGameSpins = 1_000;
        } else if (bestFitness > 1) {
            game.statistics.numberOfBaseGameSpins = 10_000;
        } else if (bestFitness > 0.1) {
            game.statistics.numberOfBaseGameSpins = 100_000;
        } else {
            game.statistics.numberOfBaseGameSpins = 1_000_000;
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
                .survivorsFraction(Math.clamp(ThreadLocalRandom.current().nextGaussian() * 0.05 + 0.05, 0.0, 1.0))
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        new UniformCrossover<>(
                                Math.clamp(ThreadLocalRandom.current().nextGaussian() * 0.2 + 0.8, 0.0, 1.0),
                                Math.clamp(ThreadLocalRandom.current().nextGaussian() * 0.1 + 0.5, 0.0, 1.0)),
                        new Mutator<>(Math.clamp(ThreadLocalRandom.current().nextGaussian() * 0.05 + 0.25, 0.0, 1.0)))
                .build();

        EvolutionStream<DoubleGene, Double> stream = null;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Path.of(INTERMEDIATE_FILE)))) {
            stream = engine.stream((EvolutionResult<DoubleGene, Double>) in.readObject());
        } catch (Exception e) {
            stream = engine.stream();
        }

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        Genotype<DoubleGene> result = stream.limit(Limits.byFitnessThreshold(STOP_THRESHOLD))
                .limit(Limits.byFixedGeneration(NUMBER_OF_GENERATIONS)).peek(intermediate -> {
                    try (ObjectOutputStream out = new ObjectOutputStream(
                            Files.newOutputStream(Path.of(INTERMEDIATE_FILE)))) {
                        out.writeObject(intermediate);
                    } catch (Exception e) {
                    }
                    System.out.println(LocalTime.now() + "\t" +
                            intermediate.generation() + "\t" +
                            (bestFitness = intermediate.bestFitness()));
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
