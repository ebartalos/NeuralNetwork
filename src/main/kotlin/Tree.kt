class Tree {
    private var root = TreeNode(10, 10)

    fun createRoot() {
        fillTreeWithAllValues(root, mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), 1)

        println("debug")

    }

    private fun fillTreeWithAllValues(node: TreeNode, possibleChildren: MutableList<Int>, value: Int) {
        if (possibleChildren.isEmpty()) return
        for (i in possibleChildren) {

            val position = possibleChildren[0]
            val child = TreeNode(position, value)
            node.children?.add(child)

            val anotherPlayer = if (value == 1) 2 else 1

            fillTreeWithAllValues(child, possibleChildren.drop(1).toMutableList(), anotherPlayer)
        }
    }

    fun removeNode() {

    }
}