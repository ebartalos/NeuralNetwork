import algorithms.Backpropagation

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network()

        val backpropagation = Backpropagation(xor, 0.0, 0.5)

        for (i in 1..300) {
            xor.evaluate()
            println(xor.output())
            backpropagation.backpropagate()
        }

        println("Training completed")

        xor.setInputs(0.0, 0.0)
        xor.evaluate()
        println(xor.output())
    }
}