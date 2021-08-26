package model

enum class EventField { OPENING, MARKING, UNMARKING, EXPLOSION, RESET }

data class Field(val row: Int, val column: Int) {
    private val neighbors = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, EventField) -> Unit>()

    var marked: Boolean = false
    var open: Boolean = false
    var mined: Boolean = false

    // Read only
    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !open
    val safe: Boolean get() = !mined
    val goalAchieved: Boolean get() = safe && open || mined && marked
    val amountOfMinedNeighbors: Int get() = neighbors.filter { it.mined }.size
    val safeNeighbors: Boolean get() = neighbors.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbors(neighbor: Field) {
        neighbors.add(neighbor)
    }

    fun onEvent(callback: (Field, EventField) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            open = true
            if (mined) {
                callbacks.forEach { it(this, EventField.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, EventField.OPENING) }
                neighbors.filter { it.closed && it.safe && safeNeighbors }.forEach { it.open() }
            }
        }
    }

    fun changeMarking() {
        if (closed) {
            marked = !marked
            val event = if (marked) EventField.MARKING else EventField.UNMARKING
            callbacks.forEach { it(this, event) }
        }
    }

    fun undermine() {
        mined = true
    }

    fun restart() {
        open = false
        mined = false
        marked = false
        callbacks.forEach { it(this, EventField.RESET) }
    }
}