package org.kiworkshop.learningfpinkotlin.sample

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.head
import org.kiworkshop.learningfpinkotlin.tail

class CommonTest : FreeSpec({
    "String" - {
        "head" - {
            "abc".head() shouldBe 'a'
        }

        "tail" - {
            "abc".tail() shouldBe "bc"
        }
    }

    "List" - {
        "head" - {
            listOf('a', 'b', 'c').head() shouldBe 'a'
        }

        "tail" - {
            listOf('a', 'b', 'c').tail() shouldContainExactly listOf('b', 'c')
        }
    }
})
