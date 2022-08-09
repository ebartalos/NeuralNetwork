object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network()
        xor.evaluate()
        println(xor.output())
    }
}