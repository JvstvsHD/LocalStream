package de.jvstvshd.localstream.client.desktop.util.activity

class NwActivityFilter {

    val filters = mutableListOf<Filter>()
    fun appliesTo(activity: NetworkActivity): Boolean {
        for (filter in filters) {
            if (filter.type == activity.activityType && (filter.value.isBlank() || activity.activiyDescription.contains(
                    filter.value
                ))
            )
                return true
        }
        return false
    }

    data class Filter(val type: NetworkActivity.ActivityType, val value: String = "")
}