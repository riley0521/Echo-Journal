package com.rpfcoding.echo_journal.journal.domain

class GetUnselectedTopicsUseCase {

    operator fun invoke(
        query: String,
        shouldTakeItems: Boolean,
        selectedTopics: Set<String>,
        allTopics: Set<String>
    ): TopicInformation {
        val unselectedTopics = if (query.isNotBlank()) {
            allTopics.filter { it.contains(query, true) }
        } else {
            if (shouldTakeItems) {
                allTopics.filter { !selectedTopics.contains(it) }.take(3)
            } else emptyList()
        }
        val isNewTopic = query.isNotBlank() && unselectedTopics.none { it.equals(query, true) }
        return TopicInformation(
            unselectedTopics = unselectedTopics.toSet(),
            isNewTopic = isNewTopic
        )
    }
}

data class TopicInformation(
    val unselectedTopics: Set<String>,
    val isNewTopic: Boolean
)