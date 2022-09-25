import ai.Network
import ai.algorithms.Genetics

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

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
                useHeHeuristics = false
            )
            networks.add(network)
            fitness[network] = 0
        }

        for (generation in 0..100) {
            println("Generation $generation")
            // amount of testing runs
            for (i in 0..100) {
                for (network1 in networks) {
                    for (network2 in networks) {
                        if (network1 == network2) continue

                        val result = tictactoe.playAI(network1, network2)
                        if (result == 1) {
                            fitness[network1] = fitness[network1]!! + 1
                            fitness[network2] = fitness[network2]!! - 1
                        } else if (result == 2) {
                            fitness[network1] = fitness[network1]!! - 1
                            fitness[network2] = fitness[network2]!! + 1
                        }
                    }
                }
            }
            sortedFitness = fitness.toList()
                .sortedBy { (key, value) -> value }
                .toMap()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true)
        }

        // Play with winner
        tictactoe.play(sortedFitness!!.keys.last())

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
}