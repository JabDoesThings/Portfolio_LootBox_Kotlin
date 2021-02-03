import jab.spigot.util.WeightTable
import org.junit.jupiter.api.Test

class TestWeightTable {

    @Test
    fun test() {
        val table = WeightTable<Int>()
        table.add(1, 50)
        table.add(2, 2)

        for (index in 1..100) {
            println("$index: ${table.roll()}")
        }
    }
}