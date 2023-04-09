package ai.algorithms

import Constants
import ai.Network
import kotlin.random.Random

/**
 * @param networks ordered list of networks by fitness (starting with best fitness)
 */
class Genetics(private val networks: List<Network>) {

    /**
     * Take two best networks and breed them.
     * Top 1% of the networks is passed down.
     * Bottom 20% of the networks are created randomly to avoid reaching local maximums.
     * Rest is filled with bred (and optionally mutated) networks.
     *
     * @param mutate determines if kids should be mutated
     * @param mutationChance percentual chance to mutation for each weight
     */
    fun breed(mutate: Boolean, mutationChance: Int = 0) {
        // let's assume network is ordered by fitness with best brains in low indexes
        val network1Weights = networks[0].weights()
        val network2Weights = networks[1].weights()

        val network1Iterator = network1Weights.listIterator()
        val network2Iterator = network2Weights.listIterator()

        while (network1Iterator.hasNext()) {
            network1Iterator.next()
            if (Math.random() > 0.5) {
                network1Iterator.set(network2Iterator.next())
            } else {
                network2Iterator.next()
            }
        }

        val passDown = (networks.size * 0.01).toInt()
        val lastFifth = (networks.size * 0.8).toInt()

        for (network in networks.subList(passDown, lastFifth)) {
            network.updateWeights(network1Weights)
        }

        if (mutate) {
            for (network in networks.subList(passDown, lastFifth)) {
                mutate(network, Constants.MUTATION_RANGE_FROM, Constants.MUTATION_RANGE_TO, mutationChance)
            }
        }

        for (network in networks.subList(lastFifth, networks.size)) {
            val randomWeights = ArrayList<Double>()
            for (i in 1..networks[0].weights().size) {
                randomWeights.add(Random.nextDouble(-2.0, 2.0))
            }
            network.updateWeights(randomWeights)
        }
    }

    /**
     * Mutate network.
     *
     * @param network neural network
     * @param from lower index of mutation range
     * @param to higher index of mutation range
     * @param chance percentual chance to mutate
     */
    private fun mutate(network: Network, from: Double, to: Double, chance: Int) {
        for (layer in network.layers) {
            for (connection in layer.outgoingConnections) {
                if (Random.nextInt() % 100 < chance) {
                    if (connection.weight == 0.0) {
                        connection.weight = Random.nextDouble(-0.01, 0.01)
                    } else if (Random.nextBoolean()) {
                        connection.weight += (connection.weight * Random.nextDouble(from, to)) - connection.weight
                    } else {
                        connection.weight -= (connection.weight * Random.nextDouble(from, to)) - connection.weight
                    }
                }
            }
        }
    }
}