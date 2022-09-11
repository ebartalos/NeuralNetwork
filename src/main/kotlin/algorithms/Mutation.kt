package algorithms

import Network
import kotlin.random.Random

class Mutation(private val network: Network) {

    fun mutate() {
        for (layer in network.layers){
            for (connection in layer.outgoingConnections){
                connection.weight = connection.weight * Random.nextDouble(0.85, 1.15)
            }
        }
    }
}