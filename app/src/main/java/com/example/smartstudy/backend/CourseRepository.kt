package com.example.smartstudy.backend

import com.example.smartstudy.model.Course
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CourseRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getCourses(): List<Course> {

        return try {

            db.collection("courses")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->

                    Course(
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        progress = (doc.getLong("progress") ?: 0L).toInt(),
                        enrolled = doc.getBoolean("enrolled") ?: false
                    )
                }

        } catch (e: Exception) {
            emptyList()
        }
    }
}