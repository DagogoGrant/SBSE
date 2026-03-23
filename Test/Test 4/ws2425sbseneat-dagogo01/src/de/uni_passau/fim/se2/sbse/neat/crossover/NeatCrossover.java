package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class NeatCrossover implements Crossover<NetworkChromosome> {

    private final Random random;

    public NeatCrossover(Random random) {
        this.random = requireNonNull(random);
    }

    @Override
public NetworkChromosome apply(NetworkChromosome parent1, NetworkChromosome parent2) {
    NetworkChromosome fitterParent = parent1.getFitness() >= parent2.getFitness() ? parent1 : parent2;
    NetworkChromosome lessFilParent = parent1.getFitness() >= parent2.getFitness() ? parent2 : parent1;

    Map<Integer, ConnectionGene> connectionGenes1 = getConnectionGenesByInnovation(fitterParent.getConnections());
    Map<Integer, ConnectionGene> connectionGenes2 = getConnectionGenesByInnovation(lessFilParent.getConnections());

    List<ConnectionGene> childConnections = new ArrayList<>();
    Map<Integer, NeuronGene> childNeurons = new HashMap<>();

    // Include all genes from the fitter parent
    for (ConnectionGene gene : connectionGenes1.values()) {
        childConnections.add(new ConnectionGene(
            getOrCreateNeuron(childNeurons, gene.getSourceNeuron()),
            getOrCreateNeuron(childNeurons, gene.getTargetNeuron()),
            gene.getWeight(),
            gene.getEnabled(),
            gene.getInnovationNumber()
        ));
    }

    // Include matching genes from the less fit parent
    for (ConnectionGene gene : connectionGenes2.values()) {
        if (connectionGenes1.containsKey(gene.getInnovationNumber())) {
            if (random.nextBoolean()) {
                // Replace the gene from the fitter parent
                childConnections.removeIf(g -> g.getInnovationNumber() == gene.getInnovationNumber());
                childConnections.add(new ConnectionGene(
                    getOrCreateNeuron(childNeurons, gene.getSourceNeuron()),
                    getOrCreateNeuron(childNeurons, gene.getTargetNeuron()),
                    gene.getWeight(),
                    gene.getEnabled(),
                    gene.getInnovationNumber()
                ));
            }
        }
    }

    Map<Double, List<NeuronGene>> layers = organizeNeuronsIntoLayers(childNeurons.values());

    return new NetworkChromosome(layers, childConnections);
}

    private Map<Integer, ConnectionGene> getConnectionGenesByInnovation(List<ConnectionGene> connections) {
        return connections.stream()
            .collect(Collectors.toMap(
                ConnectionGene::getInnovationNumber,  // Key: innovation number
                connection -> connection,             // Value: connection gene
                (existing, duplicate) -> {
                    // Decide which connection to keep when a duplicate is found
                    return existing.getEnabled() ? existing : duplicate;
                }
            ));
    }
    

    private NeuronGene getOrCreateNeuron(Map<Integer, NeuronGene> neurons, NeuronGene neuron) {
        return neurons.computeIfAbsent(neuron.getId(), id -> new NeuronGene(
            id,
            neuron.getActivationFunction(),
            neuron.getNeuronType()
        ));
    }

    private Map<Double, List<NeuronGene>> organizeNeuronsIntoLayers(Collection<NeuronGene> neurons) {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        for (NeuronGene neuron : neurons) {
            double layerDepth = getLayerDepth(neuron);
            layers.computeIfAbsent(layerDepth, k -> new ArrayList<>()).add(neuron);
        }
        return layers;
    }

    private double getLayerDepth(NeuronGene neuron) {
        switch (neuron.getNeuronType()) {
            case INPUT:
            case BIAS:
                return 0.0;
            case OUTPUT:
                return 1.0;
            case HIDDEN:
                return 0.5; // This is a simplification. You might want to implement a more sophisticated method.
            default:
                throw new IllegalArgumentException("Unknown neuron type: " + neuron.getNeuronType());
        }
    }
    
}

