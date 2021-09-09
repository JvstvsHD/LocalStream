import org.junit.jupiter.api.Test

class SleepTest {

    @Test
    fun testWait() {
        val interval: Long = 100
        val start = System.nanoTime()
        var end: Long = 0
        do {
            end = System.nanoTime()
        } while (start + interval >= end)
        println(end - start)
    }
}