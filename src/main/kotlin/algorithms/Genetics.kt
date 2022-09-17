package algorithms

import Network

class Genetics(private val networks: List<Network>) {

    fun breed(mutate: Boolean) {
        // let's assume network is ordered by fitness with best brains in low indexes
        val network1Connections = networks[0].weights()
        val network2Connections = networks[1].weights()

        val network1Iterator = network1Connections.listIterator()
        val network2Iterator = network2Connections.listIterator()

        println("N1")
        network1Connections.forEach { println("$it ") }
        println("N2")
        network2Connections.forEach { println("$it ") }


        while (network1Iterator.hasNext()) {
            network1Iterator.next()
            if (Math.random() > 0.5) {
                network1Iterator.set(network2Iterator.next())
            } else {
                network2Iterator.next()
            }
        }

        println("N1")
        network1Connections.forEach { println("$it ") }

        // todo mutation part
        println("N3 before update")
        networks[2].weights().forEach { println("$it ") }

        for (network in networks.subList(2, networks.size)) {
            network.updateWeights(network1Connections)
        }

        println("N3 after update")
        networks[2].weights().forEach { println("$it ") }
    }
}