import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron
import java.io.File

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
        var sortedFitness: Map<Network, Int>

        val naturalNumbers = generateSequence(1) { it + 1 }
        val naturalNumbersIterator = naturalNumbers.iterator()

        for (i in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = Network(
                inputNeurons = 9,
                outputNeurons = 9,
                id = naturalNumbersIterator.next()
            )
            network.addHiddenLayer(ReLuNeuron::class, 20, true)
            network.addHiddenLayer(ReLuNeuron::class, 25, true)
            network.addHiddenLayer(ReLuNeuron::class, 30, true)

            network.createConnections(true)

            networks.add(network)
            fitness[network] = 0
        }

        generationLoop@ for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                for (game in 1..50) {
                    val result =
                        tictactoe.play(network, isPlayerSecond = true, isInputRandom = true, printMessages = false)
                    if (result == 1 || result == 0) {
                        fitness[network] = fitness[network]!! + 1
                    } else if (result == 2) {
                        fitness[network] = fitness[network]!! - 1
                    }
                }
            }

            sortedFitness = fitness.toList()
                .sortedBy { (_, value) -> value }
                .toMap()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (generation % Constants.TEST_EACH_X_GENERATION == 0) {
                playWithWinner(tictactoe, sortedFitness, generation)
                println("Generation $generation \n")
            }
        }
    }

    private fun playWithWinner(tictactoe: Tictactoe, sortedFitness: Map<Network, Int>, generation: Int) {
        val match = arrayOf(0, 0, 0)
        val generationLogFile = File("src/logs/$generation")
        generationLogFile.writeText("") // delete content of file if exists
        for (i in 1..50) {
            val result = tictactoe.play(
                sortedFitness.keys.last(),
                isPlayerSecond = true,
                isInputRandom = true,
                printMessages = false,
                file = generationLogFile
            )
            if (result == 1) { // ai won
                match[0] += 1
            } else if (result == 0) { // draw
                match[1] += 1
            } else { // random won
                match[2] += 1
            }
        }
        println("AI won: ${match[0]}")
        println("Draw: ${match[1]}")
        println("Random won: ${match[2]}")

        successMap[generation] = "${match[0]}, ${match[1]}, ${match[2]}"
    }
}