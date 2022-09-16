package algorithms

import Network

class Genetics(private val networks: List<Network>) {

    fun breed(mutate: Boolean) {
        // let's assume network is ordered by fitness with best brains in low indexes
        val network1 = networks[0]
        val network2 = networks[1]

        val network1Iterator = network1.neurons.listIterator()
        val network2Iterator = network2.neurons.listIterator()

        println("N1")
        network1.neurons.forEach { println("${it.value} ") }
        println("N2")
        network2.neurons.forEach { println("${it.value} ") }


        while (network1Iterator.hasNext()) {
            network1Iterator.next()
            if (Math.random() > 0.5) {
                network1Iterator.set(network2Iterator.next())
            } else {
                network2Iterator.next()
            }
        }

        println("N1")
        network1.neurons.forEach { println("${it.value} ") }

    }
}