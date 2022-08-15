object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network()

        for (i in 1..300) {
            xor.evaluate()
            println(xor.output())
            xor.backpropagation()
        }
    }
}