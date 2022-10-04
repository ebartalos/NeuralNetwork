class Tree {

    private lateinit var root: TreeNode

    fun isRootInitialized(): Boolean {
        return ::root.isInitialized
    }

    fun createRoot(position: Int, value: Int) {
        root = TreeNode(position, value)

        val possibleChildren = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        possibleChildren.remove(position)

        val childrenValue = if (value == 1) 2 else 1

        fillTreeWithAllValues(root, possibleChildren, childrenValue)
    }

    private fun fillTreeWithAllValues(node: TreeNode, possibleChildren: MutableList<Int>, value: Int) {
        if (possibleChildren.isEmpty()) return

        for (possibleChild in possibleChildren) {

            val child = TreeNode(possibleChild, value)
            node.children.add(child)

            val anotherPlayer = if (value == 1) 2 else 1

            fillTreeWithAllValues(
                child, possibleChildren.toMutableList().also { it.remove(possibleChild) }, anotherPlayer
            )
        }
    }

//    fun traverseAndDestroy(node: TreeNode): Int {
//        if (node.children.isEmpty()) return node
//        else {
//            node
//        }
//    }
}