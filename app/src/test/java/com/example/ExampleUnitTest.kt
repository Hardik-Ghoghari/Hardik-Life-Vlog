package com.example

import com.example.domain.model.Vlog
import com.example.domain.model.VlogSortOrder
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun vlog_sorting_worksCorrectly() {
    val vlog1 = Vlog(
      id = "1",
      title = "First",
      description = "Desc",
      thumbnailUrl = "",
      videoUrl = "",
      categoryId = "cat_travel",
      categoryName = "Travel",
      publishedAt = "2 days ago",
      duration = "10:00",
      views = 1000,
      likes = 100,
      timestamp = 1000L
    )

    val vlog2 = Vlog(
      id = "2",
      title = "Second",
      description = "Desc",
      thumbnailUrl = "",
      videoUrl = "",
      categoryId = "cat_daily",
      categoryName = "Daily Life",
      publishedAt = "1 day ago",
      duration = "12:00",
      views = 5000,
      likes = 500,
      timestamp = 2000L
    )

    val list = listOf(vlog1, vlog2)
    val sortedByPopular = list.sortedByDescending { it.views }
    assertEquals("2", sortedByPopular.first().id)

    val sortedByNewest = list.sortedByDescending { it.timestamp }
    assertEquals("2", sortedByNewest.first().id)
  }
}

