package com.example.model

data class SchemaField(
    val name: String,
    val type: String,
    val constraints: String,
    val description: String
)

data class SchemaTable(
    val tableName: String,
    val purpose: String,
    val primaryKey: String,
    val fields: List<SchemaField>,
    val indices: List<String>
)

data class ApiEndpoint(
    val method: String,
    val path: String,
    val summary: String,
    val requestSample: String? = null,
    val responseSample: String
)

data class ArchitectureModule(
    val title: String,
    val techStack: String,
    val responsibilities: List<String>,
    val architecturalPattern: String
)
