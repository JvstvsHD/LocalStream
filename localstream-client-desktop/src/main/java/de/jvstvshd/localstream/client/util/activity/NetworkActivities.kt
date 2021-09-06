package de.jvstvshd.localstream.client.util.activity

import java.util.*
import java.util.stream.Collectors

class NetworkActivities {

    val activities = mutableSetOf<NetworkActivity>()

    fun addActivity(activity: NetworkActivity) {
        if (activities.any { networkActivity -> networkActivity.id == activity.id })
            throw IllegalArgumentException("A activity with the uuid ${activity.id} was already added.")
        activities.add(activity)
    }

    private fun getActivityOrCreate(uuid: UUID, type: NetworkActivity.ActivityType): NetworkActivity =
        activities.stream().filter { networkActivity -> networkActivity.id == uuid }.findAny().orElse(
            NetworkActivity(
                NetworkActivity.State.NOT_INITIALIZED,
                type,
                "",
                uuid
            )
        )

    fun ensureCreated(uuid: UUID, type: NetworkActivity.ActivityType) {
        val activity = getActivityOrCreate(uuid, type)
        when (activities.contains(activity)) {
            true -> return
            false -> addActivity(activity);
        }
    }

    fun changeActivityState(uuid: UUID, newState: NetworkActivity.State) {
        for (networkActivity in activities.filter { networkActivity -> networkActivity.id == uuid }) {
            networkActivity.activityState = newState
        }
    }

    fun changeActivityProgress(uuid: UUID, progress: Double) {
        for (networkActivity in activities.filter { networkActivity -> networkActivity.id == uuid }) {
            networkActivity.progress = progress
        }
    }

    fun filter(filter: NwActivityFilter): Set<NetworkActivity> {
        return activities.stream().filter { networkActivity -> filter.appliesTo(networkActivity) }
            .collect(Collectors.toSet())
    }
}