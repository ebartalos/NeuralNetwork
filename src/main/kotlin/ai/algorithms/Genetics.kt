package ai.algorithms

import ai.Network

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

        for (network in networks.subList(2, networks.size)) {
            network.updateWeights(network1Weights)
        }

        if (mutate) {
            for (network in networks.subList(2, networks.size)) {
                val mutation = Mutation(network)
                mutation.mutate(0.9, 1.1, mutationChance)
            }
        }
    }
}