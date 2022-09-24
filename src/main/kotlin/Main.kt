import ai.Network

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val tictactoe = Tictactoe()

        val network = Network(inputNeurons = 9, outputNeurons = 9)
        network.evaluate()

        val result = network.output()
        println(result)

        tictactoe.play()

//        val xor = Network(useHeHeuristics = true)
//        val xor2 = Network(useHeHeuristics = true)
//        val xor3 = Network(useHeHeuristics = true)
//
//        xor.evaluate()
//        xor2.evaluate()
//
//
//        val backpropagation = Backpropagation(xor, 0.0, 0.7)

//        val mutation = Mutation(xor)
//        mutation.mutate()

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