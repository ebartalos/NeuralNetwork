import ai.Network
import ai.algorithms.Backpropagation
import ai.algorithms.Genetics

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network(useHeHeuristics = true)
        val xor2 = Network(useHeHeuristics = true)
        val xor3 = Network(useHeHeuristics = true)

        xor.evaluate()
        xor2.evaluate()


        val backpropagation = Backpropagation(xor, 0.0, 0.7)

//        val mutation = Mutation(xor)
//        mutation.mutate()

//        for (i in 1..30) {
//            xor.evaluate()
//            println(xor.output())
//            backpropagation.backpropagate()
//        }

        val gen = Genetics(mutableListOf(xor, xor2, xor3))
        gen.breed(true)

//        println("Training completed")
//
//        xor.setInputs(0.0, 0.0)
//        xor.evaluate()
//        println(xor.output())
    }
}