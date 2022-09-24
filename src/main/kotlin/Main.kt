object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val tictactoe = Tictactoe()
        tictactoe.fill(1,1)
        tictactoe.fill(2,2)
        tictactoe.fill(3,1)
        tictactoe.fill(4,2)
        tictactoe.fill(6,2)
        tictactoe.fill(9,1)

        tictactoe.fill(5,1)

        tictactoe.determineWinner()
//        tictactoe.fill(1,2)
       tictactoe.prettyPrint()


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