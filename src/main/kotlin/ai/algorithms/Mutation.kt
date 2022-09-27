package ai.algorithms

import ai.Network
import kotlin.random.Random

class Mutation(private val network: Network) {

    /**
     * @param chance percentual chance to mutate
     */
    fun mutate(from: Double, to: Double, chance: Int) {
        for (layer in network.layers) {
            for (connection in layer.outgoingConnections) {
                if (Random.nextInt() % 100 < chance) {
                    connection.weight = connection.weight * Random.nextDouble(from, to)
                }
            }
        }
    }
}