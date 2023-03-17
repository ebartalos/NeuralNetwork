import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron
import ai.neurons.TanhNeuron
import snake.Snake


object MainSnake {
    @JvmStatic
    fun main(args: Array<String>) {
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0

        createNetworks(networks, fitness)

        generationLoop@ for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                startGame(network, fitness)
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                println("Generation $generation")
                println("Best network fitness ${fitness[bestNetwork]!!}")

                bestFitness = fitness[bestNetwork]!!
            }
        }
    }

    private fun createNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>){
        for (networkId in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = createNetwork(networkId)
            networks.add(network)
            fitness[network] = 0
        }
    }

    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(4)
        network.addHiddenLayer(ReLuNeuron::class, 9, true)
        network.addHiddenLayer(ReLuNeuron::class, 9, true)
        network.addHiddenLayer(ReLuNeuron::class, 9, true)
        network.addOutputLayer(TanhNeuron::class, 4)
        network.createConnections()

        return network
    }

    private fun startGame(network: Network, fitness: HashMap<Network, Int>) {
        val snake = Snake(network)
        snake.isVisible = true

        while (snake.board.isGameOver.not()) {
            snake.changeDirection()
            Thread.sleep(snake.board.delay.toLong())
        }
        fitness[network] = snake.board.snakeBodyLength
        snake.dispose()
    }
}