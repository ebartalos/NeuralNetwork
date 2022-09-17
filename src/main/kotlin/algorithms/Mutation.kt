package algorithms

import Network
import kotlin.random.Random

class Mutation(private val network: Network) {

    fun mutate(from: Double, to: Double) {
        for (layer in network.layers) {
            for (connection in layer.outgoingConnections) {
                connection.weight = connection.weight * Random.nextDouble(from, to)
            }
        }
    }
}