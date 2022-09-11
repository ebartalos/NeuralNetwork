import algorithms.Backpropagation

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network(useHeHeuristics = true)

        val backpropagation = Backpropagation(xor, 0.0, 0.7)

//        val mutation = Mutation(xor)
//        mutation.mutate()

        for (i in 1..20) {
            xor.evaluate()
            println(xor.output())
            backpropagation.backpropagate()
        }

//        println("Training completed")
//
//        xor.setInputs(0.0, 0.0)
//        xor.evaluate()
//        println(xor.output())
    }
}