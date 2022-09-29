import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron

object Main {
    private val successMap = hashMapOf<Int, String>()

    @JvmStatic
    fun main(args: Array<String>) {
        ga()
        successMap.toSortedMap().forEach { (generation, score) ->
            println("$generation - $score")
        }
    }

    private fun ga() {
        val tictactoe = Tictactoe()
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()
        val naturalNumbers = generateSequence(1) { it + 1 }
        val naturalNumbersIterator = naturalNumbers.iterator()
        var sortedFitness: Map<Network, Int>

        for (i in 0..9) {
            val network = Network(
                inputNeurons = 9,
                outputNeurons = 9,
                id = naturalNumbersIterator.next()
            )
            network.addHiddenLayer(ReLuNeuron::class, 20, true)
            network.addHiddenLayer(ReLuNeuron::class, 25, true)
            network.addHiddenLayer(ReLuNeuron::class, 25, true)

            network.createConnections(true)

            networks.add(network)
            fitness[network] = 0
        }

        generationLoop@ for (generation in 0..20000) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

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

            if (generation % 1000 == 0) {
                playWithWinner(tictactoe, sortedFitness, generation)
                println("Generation $generation")
            }
        }
    }

    private fun playWithWinner(tictactoe: Tictactoe, sortedFitness: Map<Network, Int>, generation: Int) {
        val match = arrayOf(0, 0, 0)
        for (i in 0..50) {
            val result = tictactoe.play(sortedFitness.keys.last(), isPlayerSecond = true, isInputRandom = true)
            if (result == 2) {
                match[0] += 1
            } else if (result == 0) {
                match[1] += 1
            } else {
                match[2] += 1
            }
        }
        println("AI won: ${match[0]}")
        println("Draw: ${match[1]}")
        println("Random won: ${match[2]}")

        successMap[generation] = "${match[0]}, ${match[1]}, ${match[2]}"
    }
}