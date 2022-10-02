package ai.algorithms

import Constants
import ai.Network
import kotlin.random.Random

class Genetics(private val networks: List<Network>) {

    /**
     * Takes two best networks, breeds them and fills rest of the lists with children.
     * @param mutate determines if kids should be mutated
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

        // TODO some last networks weights should be updated randomly -> new population
        for (network in networks.subList(2, networks.size)) {
            network.updateWeights(network1Weights)
        }

        if (mutate) {
            for (network in networks.subList(2, networks.size - 2)) {
                val mutation = Mutation(network)
                mutation.mutate(Constants.MUTATION_RANGE_FROM, Constants.MUTATION_RANGE_TO, mutationChance)
            }
            for (network in networks.subList(networks.size - 2, networks.size)) {
                val randomWeights = ArrayList<Double>()
                for (i in 1..networks[0].weights().size) {
                    randomWeights.add(Random.nextDouble(-2.0, 2.0))
                }
                network.updateWeights(randomWeights)
            }
        }
    }
}