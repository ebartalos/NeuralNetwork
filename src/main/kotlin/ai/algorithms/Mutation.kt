package ai.algorithms

import ai.Network
import kotlin.random.Random

/**
 * Mutation for genetic algorithms
 */
class Mutation(private val network: Network) {
    /**
     * @param from lower index of mutation range
     * @param to higher index of mutation range
     * @param chance percentual chance to mutate
     */
    fun mutate(from: Double, to: Double, chance: Int) {
        for (layer in network.layers) {
            for (connection in layer.outgoingConnections) {
                if (Random.nextInt() % 100 < chance) {
                    if (connection.weight == 0.0) {
                        connection.weight = Random.nextDouble(-0.01, 0.01)
                    } else {
                        val mutationPart = (connection.weight * Random.nextDouble(from, to)) - connection.weight
                        if (Random.nextBoolean()) {
                            connection.weight += mutationPart
                        } else {
                            connection.weight -= mutationPart
                        }
                    }
                }
            }
        }
    }
}