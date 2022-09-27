import ai.Network
import ai.algorithms.Genetics

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        ga()
    }

    private fun ga() {
        val tictactoe = Tictactoe()
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()
        val naturalNumbers = generateSequence(1) { it + 1 }
        val naturalNumbersIterator = naturalNumbers.iterator()
        var sortedFitness: Map<Network, Int>? = null

        for (i in 0..9) {
            val network = Network(
                inputNeurons = 9,
                outputNeurons = 9,
                id = naturalNumbersIterator.next(),
                useHeHeuristics = true
            )
            networks.add(network)
            fitness[network] = 0
        }

        generationLoop@ for (generation in 0..10000) {
            if (generation % 100 == 0) println("Generation $generation")
            // amount of testing runs
            for (network1 in networks) {
                for (network2 in networks) {
                    if (network1 == network2) continue

                    val result = tictactoe.playAI(network1, network2)
                    if (result == 1 || result == 0) {
                        fitness[network1] = fitness[network1]!! + 1
                    } else if (result == 2) {
                        fitness[network1] = fitness[network1]!! - 1
                    }
                }
            }

            sortedFitness = fitness.toList()
                .sortedBy { (_, value) -> value }
                .toMap()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true)

            fitness.replaceAll { _, _ -> 0 }
        }

        // Play with winner
        for (i in 0..8) {
            tictactoe.play(sortedFitness!!.keys.last(), isPlayerSecond = true)
        }
    }

//        val xor = Network(useHeHeuristics = true)
//        val xor2 = Network(useHeHeuristics = true)
//        val xor3 = Network(useHeHeuristics = true)
//
//        xor.evaluate()
//        xor2.evaluate()
//
//
//        val backpropagation = Backpropagation(xor, 0.0, 0.7)

//        for (i in 1..30) {
//            xor.evaluate()
//            println(xor.output())
//            backpropagation.backpropagate()
//        }

//        val gen = Genetics(mutableListOf(xor, xor2, xor3))
//        gen.breed(true)

//        println("Training completed")
//
//        xor.setInputs(0.0, 0.0)
//        xor.evaluate()
//        println(xor.output())
}