import ai.Network
import ai.neurons.ReLuNeuron
import ai.neurons.TanhNeuron
import snake.Snake

object MainSnake {
    @JvmStatic
    fun main(args: Array<String>) {

        val network = Network(id = 1)
        network.addInputLayer(5)
        network.addHiddenLayer(ReLuNeuron::class, 18, true)
        network.addHiddenLayer(ReLuNeuron::class, 22, true)
        network.addOutputLayer(TanhNeuron::class, 4)
        network.createConnections()

        startGame(network)
    }

    private fun startGame(network: Network) {
        val snake = Snake(network)
        snake.isVisible = true

        while (snake.board.isGameOver.not()) {
            snake.changeDirection()
            Thread.sleep(100)
        }

    }
}