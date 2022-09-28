import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron

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
                id = naturalNumbersIterator.next()
            )
            network.addHiddenLayer(ReLuNeuron::class, 30, true)
            network.addHiddenLayer(ReLuNeuron::class, 40, true)
            network.addHiddenLayer(ReLuNeuron::class, 40, true)

            network.createConnections(true)

            networks.add(network)
            fitness[network] = 0
        }

        generationLoop@ for (generation in 0..1000) {
            if (generation % 100 == 0) {
                println("Generation $generation")
            }

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
            genetics.breed(mutate = true, mutationChance = 10)

            // reset fitness
            fitness.replaceAll { _, _ -> 0 }
        }

        // Play with winner
        val match = arrayOf(0, 0, 0)
        for (i in 0..50) {
            val result = tictactoe.play(sortedFitness!!.keys.last(), isPlayerSecond = true, isInputRandom = true)
            if (result == 2) {
                match[0] += 1
            } else if (result == 0) {
                match[1] += 1
            } else {
                match[2] += 1
            }
        }
        println("AI won: ${match[0]}")
        println("Draw: ${match[0]}")
        println("Random won: ${match[0]}")
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