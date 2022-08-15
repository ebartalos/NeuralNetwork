object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val xor = Network()
        xor.evaluate()
        println(xor.output())
//        xor.backpropagation(xor.output()[0])
//        xor.evaluate()
//        println(xor.output())
//
////        xor.backpropagation(xor.output()[0])
//        xor.evaluate()
//        println(xor.output())
//
////        xor.backpropagation(xor.output()[0])
//        xor.evaluate()
//        println(xor.output())
//
////        xor.backpropagation(xor.output()[0])
//        xor.evaluate()
//        println(xor.output())
    }
}